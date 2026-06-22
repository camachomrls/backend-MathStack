package com.mathstack.store.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class ItemType(val id: Int, val name: String)

data class StoreItem(
    val id: UUID,
    val itemTypeId: Int,
    val name: String,
    val cost: Int,
    val assetUrl: String,
)

data class UserInventoryItem(
    val userId: UUID,
    val itemId: UUID,
    val isEquipped: Boolean,
    val acquiredAt: LocalDateTime,
)
