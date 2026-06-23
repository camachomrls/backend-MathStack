package com.mathstack.admin.infrastructure.rest.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatsResponse(
    val totalUsers: Int,
    val activeChallenges: Int
)

@Serializable
data class OverviewStatsResponse(
    val cpuUsagePercent: Double,
    val memoryUsageMb: Long
)

@Serializable
data class UpdateCoinsRequest(
    val coins: Int
)

@Serializable
data class GenerateAvatarRequest(
    val seed: String,
    val style: String = "bottts"
)

@Serializable
data class GenerateAvatarResponse(
    val avatarUrl: String
)
