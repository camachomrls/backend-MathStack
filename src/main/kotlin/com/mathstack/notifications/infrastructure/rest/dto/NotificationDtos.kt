package com.mathstack.notifications.infrastructure.rest.dto

import com.mathstack.notifications.domain.model.NotificationPreferences
import com.mathstack.notifications.domain.model.UserDeviceToken
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNotificationPreferencesRequest(
    val preferredTimeInUtc: String,
    val isEnabled: Boolean
)

@Serializable
data class NotificationPreferencesResponse(
    val userId: String,
    val preferredTimeInUtc: String,
    val isEnabled: Boolean
)

@Serializable
data class RegisterDeviceTokenRequest(
    val fcmToken: String
)

@Serializable
data class UserDeviceTokenResponse(
    val userId: String,
    val fcmToken: String,
    val updatedAt: String
)

fun NotificationPreferences.toResponse() = NotificationPreferencesResponse(
    userId = userId.toString(),
    preferredTimeInUtc = preferredTimeInUtc,
    isEnabled = isEnabled
)

fun UserDeviceToken.toResponse() = UserDeviceTokenResponse(
    userId = userId.toString(),
    fcmToken = fcmToken,
    updatedAt = updatedAt.toString()
)
