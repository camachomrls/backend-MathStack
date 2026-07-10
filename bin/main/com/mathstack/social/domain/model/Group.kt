package com.mathstack.social.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Group(
    val id: UUID,
    val name: String,
    val description: String?,
    val subject: String,
    val maxMembers: Int,
    val creatorId: UUID,
    val createdAt: LocalDateTime,
    val activeChallenges: Int = 0,
    val totalXp: Int = 0,
    val color: String = "from-blue-500 to-blue-600",
    val activeLevelId: UUID? = null
)
