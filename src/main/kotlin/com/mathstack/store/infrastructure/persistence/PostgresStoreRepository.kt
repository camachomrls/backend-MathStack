package com.mathstack.store.infrastructure.persistence

import com.mathstack.shared.domain.exception.BusinessRuleException
import com.mathstack.shared.domain.exception.ConflictException
import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.store.domain.model.ItemType
import com.mathstack.store.domain.model.StoreItem
import com.mathstack.store.domain.model.UserInventoryItem
import com.mathstack.store.domain.repository.StoreRepository
import com.mathstack.users.infrastructure.persistence.UserGamificationStatsTable
import com.mathstack.users.infrastructure.persistence.UserTable
import java.time.LocalDateTime
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PostgresStoreRepository : StoreRepository {
    override fun createItemType(name: String): ItemType = transaction {
        val id = ItemTypeTable.insert { it[ItemTypeTable.name] = name } get ItemTypeTable.id
        ItemTypeTable.selectAll().where { ItemTypeTable.id eq id }.single().toItemType()
    }

    override fun findItemTypeById(id: Int): ItemType? = transaction {
        ItemTypeTable.selectAll().where { ItemTypeTable.id eq id }.singleOrNull()?.toItemType()
    }

    override fun listItemTypes(): List<ItemType> = transaction { ItemTypeTable.selectAll().map { it.toItemType() } }

    override fun createStoreItem(item: StoreItem): StoreItem = transaction {
        StoreItemTable.insert {
            it[id] = item.id
            it[itemTypeId] = item.itemTypeId
            it[name] = item.name
            it[cost] = item.cost
            it[assetUrl] = item.assetUrl
        }
        findStoreItemByIdInTransaction(item.id)!!
    }

    override fun findStoreItemById(id: UUID): StoreItem? = transaction { findStoreItemByIdInTransaction(id) }

    override fun listStoreItems(): List<StoreItem> = transaction { StoreItemTable.selectAll().map { it.toStoreItem() } }

    override fun updateStoreItem(item: StoreItem): StoreItem? = transaction {
        val updated = StoreItemTable.update({ StoreItemTable.id eq item.id }) {
            it[itemTypeId] = item.itemTypeId
            it[name] = item.name
            it[cost] = item.cost
            it[assetUrl] = item.assetUrl
        }
        if (updated == 0) null else findStoreItemByIdInTransaction(item.id)
    }

    override fun deleteStoreItem(id: UUID): Boolean = transaction { StoreItemTable.deleteWhere { StoreItemTable.id eq id } > 0 }

    override fun listInventory(userId: UUID): List<UserInventoryItem> = transaction {
        UserInventoryTable.selectAll().where { UserInventoryTable.userId eq userId }.map { it.toUserInventoryItem() }
    }

    override fun buyItem(userId: UUID, itemId: UUID): UserInventoryItem = transaction {
        UserTable.selectAll().where { UserTable.id eq userId }.singleOrNull()
            ?: throw NotFoundException("User $userId was not found")
        val item = findStoreItemByIdInTransaction(itemId)
            ?: throw NotFoundException("Store item $itemId was not found")
        val alreadyOwned = UserInventoryTable.selectAll()
            .where { (UserInventoryTable.userId eq userId) and (UserInventoryTable.itemId eq itemId) }
            .singleOrNull() != null
        if (alreadyOwned) throw ConflictException("User already owns this item")

        val stats = UserGamificationStatsTable.selectAll()
            .where { UserGamificationStatsTable.userId eq userId }
            .singleOrNull()
            ?: throw NotFoundException("Gamification stats for user $userId were not found")
        val currentCoins = stats[UserGamificationStatsTable.coins]
        if (currentCoins < item.cost) {
            throw BusinessRuleException("Insufficient coins to buy this item")
        }

        UserGamificationStatsTable.update({ UserGamificationStatsTable.userId eq userId }) {
            it[coins] = currentCoins - item.cost
        }
        val acquiredAt = LocalDateTime.now()
        UserInventoryTable.insert {
            it[UserInventoryTable.userId] = userId
            it[UserInventoryTable.itemId] = itemId
            it[isEquipped] = false
            it[UserInventoryTable.acquiredAt] = acquiredAt
        }
        UserInventoryItem(userId, itemId, isEquipped = false, acquiredAt = acquiredAt)
    }

    override fun equipItem(userId: UUID, itemId: UUID): UserInventoryItem? = transaction {
        val item = findStoreItemByIdInTransaction(itemId) ?: return@transaction null
        val owned = UserInventoryTable.selectAll()
            .where { (UserInventoryTable.userId eq userId) and (UserInventoryTable.itemId eq itemId) }
            .singleOrNull() ?: return@transaction null

        val sameTypeItems = StoreItemTable.selectAll()
            .where { StoreItemTable.itemTypeId eq item.itemTypeId }
            .map { it[StoreItemTable.id] }

        sameTypeItems.forEach { sameTypeItemId ->
            UserInventoryTable.update({
                (UserInventoryTable.userId eq userId) and (UserInventoryTable.itemId eq sameTypeItemId)
            }) { it[isEquipped] = false }
        }
        UserInventoryTable.update({
            (UserInventoryTable.userId eq userId) and (UserInventoryTable.itemId eq itemId)
        }) { it[isEquipped] = true }

        UserInventoryItem(userId, itemId, isEquipped = true, acquiredAt = owned[UserInventoryTable.acquiredAt])
    }

    private fun findStoreItemByIdInTransaction(id: UUID): StoreItem? =
        StoreItemTable.selectAll().where { StoreItemTable.id eq id }.singleOrNull()?.toStoreItem()
}
