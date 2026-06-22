package com.mathstack.store.domain.repository

import com.mathstack.store.domain.model.ItemType
import com.mathstack.store.domain.model.StoreItem
import com.mathstack.store.domain.model.UserInventoryItem
import java.util.UUID

interface StoreRepository {
    fun createItemType(name: String): ItemType
    fun findItemTypeById(id: Int): ItemType?
    fun listItemTypes(): List<ItemType>

    fun createStoreItem(item: StoreItem): StoreItem
    fun findStoreItemById(id: UUID): StoreItem?
    fun listStoreItems(): List<StoreItem>
    fun updateStoreItem(item: StoreItem): StoreItem?
    fun deleteStoreItem(id: UUID): Boolean

    fun listInventory(userId: UUID): List<UserInventoryItem>
    fun buyItem(userId: UUID, itemId: UUID): UserInventoryItem
    fun equipItem(userId: UUID, itemId: UUID): UserInventoryItem?
}
