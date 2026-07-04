package com.mathstack.admin.domain.repository

import com.mathstack.admin.domain.model.AdminSettings

interface AdminSettingsRepository {
    fun getSettings(): AdminSettings
    fun updateSettings(settings: AdminSettings): AdminSettings
}
