package com.mathstack.store.infrastructure.persistence

import com.mathstack.store.domain.model.ItemType
import com.mathstack.store.domain.model.StoreItem
import com.mathstack.store.domain.model.UserInventoryItem
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toItemType(): ItemType = ItemType(this[ItemTypeTable.id], this[ItemTypeTable.name])

fun ResultRow.toStoreItem(): StoreItem =
    StoreItem(this[StoreItemTable.id], this[StoreItemTable.itemTypeId], this[StoreItemTable.name], this[StoreItemTable.cost], this[StoreItemTable.assetUrl])

fun ResultRow.toUserInventoryItem(): UserInventoryItem =
    UserInventoryItem(this[UserInventoryTable.userId], this[UserInventoryTable.itemId], this[UserInventoryTable.isEquipped], this[UserInventoryTable.acquiredAt])
