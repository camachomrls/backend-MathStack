package com.mathstack.store.application

import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.store.domain.model.ItemType
import com.mathstack.store.domain.model.StoreItem
import com.mathstack.store.domain.model.UserInventoryItem
import com.mathstack.store.domain.repository.StoreRepository
import java.util.UUID

class CreateItemTypeUseCase(private val repository: StoreRepository) {
    operator fun invoke(name: String): ItemType = repository.createItemType(name.trim())
}

class ListItemTypesUseCase(private val repository: StoreRepository) {
    operator fun invoke(): List<ItemType> = repository.listItemTypes()
}

class CreateStoreItemUseCase(private val repository: StoreRepository) {
    operator fun invoke(command: CreateStoreItemCommand): StoreItem {
        repository.findItemTypeById(command.itemTypeId)
            ?: throw NotFoundException("Item type ${command.itemTypeId} was not found")
        return repository.createStoreItem(
            StoreItem(UUID.randomUUID(), command.itemTypeId, command.name.trim(), command.cost, command.assetUrl.trim()),
        )
    }
}

class ListStoreItemsUseCase(private val repository: StoreRepository) {
    operator fun invoke(): List<StoreItem> = repository.listStoreItems()
}

class BuyItemUseCase(private val repository: StoreRepository) {
    operator fun invoke(userId: UUID, itemId: UUID): UserInventoryItem = repository.buyItem(userId, itemId)
}

class EquipItemUseCase(private val repository: StoreRepository) {
    operator fun invoke(userId: UUID, itemId: UUID): UserInventoryItem =
        repository.equipItem(userId, itemId)
            ?: throw NotFoundException("Inventory item $itemId for user $userId was not found")
}

class ListInventoryUseCase(private val repository: StoreRepository) {
    operator fun invoke(userId: UUID): List<UserInventoryItem> = repository.listInventory(userId)
}

data class CreateStoreItemCommand(
    val itemTypeId: Int,
    val name: String,
    val cost: Int,
    val assetUrl: String,
)
