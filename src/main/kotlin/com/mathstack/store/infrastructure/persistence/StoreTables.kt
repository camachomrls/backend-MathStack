package com.mathstack.store.infrastructure.persistence

import com.mathstack.users.infrastructure.persistence.UserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ItemTypeTable : Table("item_types") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

object StoreItemTable : Table("store_items") {
    val id = uuid("id")
    val itemTypeId = integer("item_type_id").references(ItemTypeTable.id)
    val name = varchar("name", 100)
    val cost = integer("cost")
    val assetUrl = varchar("asset_url", 255)
    override val primaryKey = PrimaryKey(id)
}

object UserInventoryTable : Table("user_inventory") {
    val userId = uuid("user_id").references(UserTable.id)
    val itemId = uuid("item_id").references(StoreItemTable.id)
    val isEquipped = bool("is_equipped")
    val acquiredAt = datetime("acquired_at")
    override val primaryKey = PrimaryKey(userId, itemId)
}
