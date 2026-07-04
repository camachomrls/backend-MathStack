package com.mathstack.admin.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminSettings(
    val emailNotifications: Boolean = true,
    val challengeAlerts: Boolean = true
)
