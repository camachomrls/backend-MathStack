package com.mathstack.notifications.application

import com.mathstack.notifications.domain.model.NotificationPreferences
import com.mathstack.notifications.domain.model.UserDeviceToken
import com.mathstack.notifications.domain.repository.NotificationRepository
import java.time.LocalDateTime
import java.util.UUID

class UpdateNotificationPreferencesUseCase(private val repository: NotificationRepository) {
    operator fun invoke(command: UpdateNotificationPreferencesCommand): NotificationPreferences {
        return repository.updatePreferences(
            NotificationPreferences(
                userId = command.userId,
                preferredTimeInUtc = command.preferredTimeInUtc,
                isEnabled = command.isEnabled
            )
        )
    }
}

class RegisterDeviceTokenUseCase(private val repository: NotificationRepository) {
    operator fun invoke(command: RegisterDeviceTokenCommand): UserDeviceToken {
        return repository.registerDeviceToken(
            UserDeviceToken(
                userId = command.userId,
                fcmToken = command.fcmToken,
                updatedAt = LocalDateTime.now()
            )
        )
    }
}

data class UpdateNotificationPreferencesCommand(
    val userId: UUID,
    val preferredTimeInUtc: String,
    val isEnabled: Boolean
)

data class RegisterDeviceTokenCommand(
    val userId: UUID,
    val fcmToken: String
)
