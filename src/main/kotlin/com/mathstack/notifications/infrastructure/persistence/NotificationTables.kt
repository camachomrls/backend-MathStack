package com.mathstack.notifications.infrastructure.persistence

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object NotificationPreferencesTable : Table("notification_preferences") {
    val userId = uuid("user_id")
    val preferredTimeInUtc = varchar("preferred_time_in_utc", 50)
    val isEnabled = bool("is_enabled")
    
    override val primaryKey = PrimaryKey(userId)
}

object UserDeviceTokensTable : Table("user_device_tokens") {
    val userId = uuid("user_id")
    val fcmToken = varchar("fcm_token", 255)
    val updatedAt = datetime("updated_at")
    
    override val primaryKey = PrimaryKey(userId, fcmToken)
}
