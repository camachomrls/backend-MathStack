package com.mathstack.admin.application

import com.mathstack.admin.domain.model.AdminSettings
import com.mathstack.admin.domain.repository.AdminSettingsRepository

class UpdateAdminSettingsUseCase(private val repository: AdminSettingsRepository) {
    operator fun invoke(settings: AdminSettings): AdminSettings {
        return repository.updateSettings(settings)
    }
}
