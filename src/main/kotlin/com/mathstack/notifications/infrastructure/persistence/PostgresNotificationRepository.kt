package com.mathstack.notifications.infrastructure.persistence

import com.mathstack.notifications.domain.model.NotificationPreferences
import com.mathstack.notifications.domain.model.UserDeviceToken
import com.mathstack.notifications.domain.repository.NotificationRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.and
import java.util.UUID

class PostgresNotificationRepository : NotificationRepository {
    override fun updatePreferences(preferences: NotificationPreferences): NotificationPreferences = transaction {
        val existing = NotificationPreferencesTable.selectAll().where { NotificationPreferencesTable.userId eq preferences.userId }.singleOrNull()
        if (existing != null) {
            NotificationPreferencesTable.update({ NotificationPreferencesTable.userId eq preferences.userId }) {
                it[preferredTimeInUtc] = preferences.preferredTimeInUtc
                it[isEnabled] = preferences.isEnabled
            }
        } else {
            NotificationPreferencesTable.insert {
                it[userId] = preferences.userId
                it[preferredTimeInUtc] = preferences.preferredTimeInUtc
                it[isEnabled] = preferences.isEnabled
            }
        }
        getPreferences(preferences.userId)!!
    }

    override fun getPreferences(userId: UUID): NotificationPreferences? = transaction {
        NotificationPreferencesTable.selectAll().where { NotificationPreferencesTable.userId eq userId }
            .singleOrNull()?.toNotificationPreferences()
    }

    override fun registerDeviceToken(token: UserDeviceToken): UserDeviceToken = transaction {
        val existing = UserDeviceTokensTable.selectAll().where { 
            (UserDeviceTokensTable.userId eq token.userId) and (UserDeviceTokensTable.fcmToken eq token.fcmToken) 
        }.singleOrNull()
        
        if (existing != null) {
            UserDeviceTokensTable.update({ 
                (UserDeviceTokensTable.userId eq token.userId) and (UserDeviceTokensTable.fcmToken eq token.fcmToken) 
            }) {
                it[updatedAt] = token.updatedAt
            }
        } else {
            UserDeviceTokensTable.insert {
                it[userId] = token.userId
                it[fcmToken] = token.fcmToken
                it[updatedAt] = token.updatedAt
            }
        }
        UserDeviceTokensTable.selectAll().where { 
            (UserDeviceTokensTable.userId eq token.userId) and (UserDeviceTokensTable.fcmToken eq token.fcmToken) 
        }.single().toUserDeviceToken()
    }

    override fun getDeviceTokens(userId: UUID): List<UserDeviceToken> = transaction {
        UserDeviceTokensTable.selectAll().where { UserDeviceTokensTable.userId eq userId }
            .map { it.toUserDeviceToken() }
    }
}

fun ResultRow.toNotificationPreferences() = NotificationPreferences(
    userId = this[NotificationPreferencesTable.userId],
    preferredTimeInUtc = this[NotificationPreferencesTable.preferredTimeInUtc],
    isEnabled = this[NotificationPreferencesTable.isEnabled]
)

fun ResultRow.toUserDeviceToken() = UserDeviceToken(
    userId = this[UserDeviceTokensTable.userId],
    fcmToken = this[UserDeviceTokensTable.fcmToken],
    updatedAt = this[UserDeviceTokensTable.updatedAt]
)
