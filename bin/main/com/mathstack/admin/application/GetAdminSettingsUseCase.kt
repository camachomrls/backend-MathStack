package com.mathstack.admin.application

import com.mathstack.admin.domain.model.AdminSettings
import com.mathstack.admin.domain.repository.AdminSettingsRepository

class GetAdminSettingsUseCase(private val repository: AdminSettingsRepository) {
    operator fun invoke(): AdminSettings {
        return repository.getSettings()
    }
}
