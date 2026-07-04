package com.mathstack.admin.infrastructure.persistence

import com.mathstack.admin.domain.model.AdminSettings
import com.mathstack.admin.domain.repository.AdminSettingsRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresAdminSettingsRepository : AdminSettingsRepository {
    
    private fun getSetting(keyName: String, defaultValue: Boolean): Boolean {
        return transaction {
            val record = AdminSettingsTable.selectAll().where { AdminSettingsTable.key eq keyName }.singleOrNull()
            record?.let { it[AdminSettingsTable.value].toBoolean() } ?: defaultValue
        }
    }
    
    private fun setSetting(keyName: String, valueToSet: Boolean) {
        transaction {
            val exists = AdminSettingsTable.selectAll().where { AdminSettingsTable.key eq keyName }.count() > 0
            if (exists) {
                AdminSettingsTable.update({ AdminSettingsTable.key eq keyName }) {
                    it[value] = valueToSet.toString()
                }
            } else {
                AdminSettingsTable.insert {
                    it[key] = keyName
                    it[value] = valueToSet.toString()
                }
            }
        }
    }

    override fun getSettings(): AdminSettings {
        return AdminSettings(
            emailNotifications = getSetting("emailNotifications", true),
            challengeAlerts = getSetting("challengeAlerts", true)
        )
    }

    override fun updateSettings(settings: AdminSettings): AdminSettings {
        setSetting("emailNotifications", settings.emailNotifications)
        setSetting("challengeAlerts", settings.challengeAlerts)
        return settings
    }
}
