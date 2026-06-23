package com.mathstack.notifications.domain.repository

import com.mathstack.notifications.domain.model.NotificationPreferences
import com.mathstack.notifications.domain.model.UserDeviceToken
import java.util.UUID

interface NotificationRepository {
    fun updatePreferences(preferences: NotificationPreferences): NotificationPreferences
    fun getPreferences(userId: UUID): NotificationPreferences?
    fun registerDeviceToken(token: UserDeviceToken): UserDeviceToken
    fun getDeviceTokens(userId: UUID): List<UserDeviceToken>
}
