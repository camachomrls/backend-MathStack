package com.mathstack.notifications.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class NotificationPreferences(
    val userId: UUID,
    val preferredTimeInUtc: String,
    val isEnabled: Boolean
)

data class UserDeviceToken(
    val userId: UUID,
    val fcmToken: String,
    val updatedAt: LocalDateTime
)
