package com.mathstack.users.domain.model

data class UserProfile(
    val user: User,
    val gamificationStats: UserGamificationStats,
)
