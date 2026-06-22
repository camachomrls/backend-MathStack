package com.mathstack.store.infrastructure.rest.dto

import com.mathstack.shared.domain.exception.ValidationException
import com.mathstack.store.application.CreateStoreItemCommand
import com.mathstack.store.domain.model.ItemType
import com.mathstack.store.domain.model.StoreItem
import com.mathstack.store.domain.model.UserInventoryItem
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable data class CreateItemTypeRequest(val name: String)
@Serializable data class CreateStoreItemRequest(val itemTypeId: Int, val name: String, val cost: Int, val assetUrl: String)
@Serializable data class ItemActionRequest(val itemId: String)

@Serializable data class ItemTypeResponse(val id: Int, val name: String)
@Serializable data class StoreItemResponse(val id: String, val itemTypeId: Int, val name: String, val cost: Int, val assetUrl: String)
@Serializable data class UserInventoryItemResponse(val userId: String, val itemId: String, val isEquipped: Boolean, val acquiredAt: String)

fun CreateItemTypeRequest.validName(): String {
    if (name.trim().length !in 2..50) throw ValidationException("name must contain between 2 and 50 characters")
    return name.trim()
}

fun CreateStoreItemRequest.toCommand(): CreateStoreItemCommand {
    if (itemTypeId <= 0) throw ValidationException("itemTypeId must be positive")
    if (name.trim().length !in 2..100) throw ValidationException("name must contain between 2 and 100 characters")
    if (cost < 0) throw ValidationException("cost must be greater than or equal to 0")
    if (assetUrl.trim().isBlank()) throw ValidationException("assetUrl is required")
    return CreateStoreItemCommand(itemTypeId, name.trim(), cost, assetUrl.trim())
}

fun ItemActionRequest.itemUuid(): UUID = itemId.toUuid("itemId")

fun ItemType.toResponse(): ItemTypeResponse = ItemTypeResponse(id, name)
fun StoreItem.toResponse(): StoreItemResponse = StoreItemResponse(id.toString(), itemTypeId, name, cost, assetUrl)
fun UserInventoryItem.toResponse(): UserInventoryItemResponse =
    UserInventoryItemResponse(userId.toString(), itemId.toString(), isEquipped, acquiredAt.toString())

fun String.toUuid(field: String): UUID =
    runCatching { UUID.fromString(this) }
        .getOrElse { throw ValidationException("$field must be a valid UUID") }
