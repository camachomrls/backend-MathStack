package com.mathstack.social.infrastructure.rest.dto

import com.mathstack.social.domain.model.Challenge
import com.mathstack.social.domain.model.ChallengeParticipant
import com.mathstack.social.domain.model.FriendRequest
import kotlinx.serialization.Serializable

@Serializable
data class SendFriendRequest(
    val friendId: String
)

@Serializable
data class FriendRequestResponse(
    val userId: String,
    val friendId: String,
    val status: String
)

@Serializable
data class CreateChallengeRequest(
    val exerciseId: String,
    val friendIds: List<String>
)

@Serializable
data class ChallengeResponse(
    val id: String,
    val creatorId: String,
    val exerciseId: String,
    val status: String
)

@Serializable
data class SubmitChallengeResultRequest(
    val score: Int,
    val timeTakenSeconds: Int
)

@Serializable
data class ChallengeParticipantResponse(
    val challengeId: String,
    val userId: String,
    val score: Int,
    val timeTakenSeconds: Int,
    val completedAt: String?
)

fun FriendRequest.toResponse() = FriendRequestResponse(
    userId = userId.toString(),
    friendId = friendId.toString(),
    status = status
)

fun Challenge.toResponse() = ChallengeResponse(
    id = id.toString(),
    creatorId = creatorId.toString(),
    exerciseId = exerciseId.toString(),
    status = status
)

fun ChallengeParticipant.toResponse() = ChallengeParticipantResponse(
    challengeId = challengeId.toString(),
    userId = userId.toString(),
    score = score,
    timeTakenSeconds = timeTakenSeconds,
    completedAt = completedAt?.toString()
)
