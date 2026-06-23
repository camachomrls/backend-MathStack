package com.mathstack.social.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class FriendRequest(
    val userId: UUID,
    val friendId: UUID,
    val status: String // "PENDING", "ACCEPTED"
)

data class Challenge(
    val id: UUID,
    val creatorId: UUID,
    val exerciseId: UUID,
    val status: String // "ACTIVE", "COMPLETED"
)

data class ChallengeParticipant(
    val challengeId: UUID,
    val userId: UUID,
    val score: Int,
    val timeTakenSeconds: Int,
    val completedAt: LocalDateTime?
)
