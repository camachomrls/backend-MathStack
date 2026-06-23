package com.mathstack.social.application

import com.mathstack.shared.domain.exception.BusinessRuleException
import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.social.domain.model.Challenge
import com.mathstack.social.domain.model.ChallengeParticipant
import com.mathstack.social.domain.model.FriendRequest
import com.mathstack.social.domain.repository.SocialRepository
import java.time.LocalDateTime
import java.util.UUID

class SendFriendRequestUseCase(private val repository: SocialRepository) {
    operator fun invoke(command: SendFriendRequestCommand): FriendRequest {
        val existing = repository.getFriendRequest(command.userId, command.friendId)
        if (existing != null) {
            throw BusinessRuleException("Friend request already exists")
        }
        return repository.sendFriendRequest(
            FriendRequest(userId = command.userId, friendId = command.friendId, status = "PENDING")
        )
    }
}

class AcceptFriendRequestUseCase(private val repository: SocialRepository) {
    operator fun invoke(userId: UUID, friendId: UUID): FriendRequest {
        val request = repository.getFriendRequest(userId, friendId) ?: throw NotFoundException("Friend request not found")
        if (request.status == "ACCEPTED") {
            throw BusinessRuleException("Friend request already accepted")
        }
        return repository.updateFriendRequest(request.copy(status = "ACCEPTED"))
    }
}

class ListFriendsUseCase(private val repository: SocialRepository) {
    operator fun invoke(userId: UUID): List<UUID> {
        return repository.listFriends(userId)
    }
}

class CreateChallengeUseCase(private val repository: SocialRepository) {
    operator fun invoke(command: CreateChallengeCommand): Challenge {
        val challenge = repository.createChallenge(
            Challenge(
                id = UUID.randomUUID(),
                creatorId = command.creatorId,
                exerciseId = command.exerciseId,
                status = "ACTIVE"
            )
        )
        command.friendIds.forEach { friendId ->
            repository.addChallengeParticipant(
                ChallengeParticipant(
                    challengeId = challenge.id,
                    userId = friendId,
                    score = 0,
                    timeTakenSeconds = 0,
                    completedAt = null
                )
            )
        }
        return challenge
    }
}

class SubmitChallengeResultUseCase(private val repository: SocialRepository) {
    operator fun invoke(command: SubmitChallengeResultCommand): ChallengeParticipant {
        val challenge = repository.getChallenge(command.challengeId) ?: throw NotFoundException("Challenge not found")
        if (challenge.status == "COMPLETED") {
            throw BusinessRuleException("Challenge is already completed")
        }

        val participant = repository.getChallengeParticipant(command.challengeId, command.userId)
            ?: throw NotFoundException("Participant not found in challenge")

        val updatedParticipant = repository.updateChallengeParticipant(
            participant.copy(
                score = command.score,
                timeTakenSeconds = command.timeTakenSeconds,
                completedAt = LocalDateTime.now()
            )
        )

        val allParticipants = repository.getChallengeParticipants(command.challengeId)
        val allCompleted = allParticipants.all { it.completedAt != null }

        if (allCompleted) {
            repository.updateChallenge(challenge.copy(status = "COMPLETED"))
        }

        return updatedParticipant
    }
}

data class SendFriendRequestCommand(
    val userId: UUID,
    val friendId: UUID
)

data class CreateChallengeCommand(
    val creatorId: UUID,
    val exerciseId: UUID,
    val friendIds: List<UUID>
)

data class SubmitChallengeResultCommand(
    val challengeId: UUID,
    val userId: UUID,
    val score: Int,
    val timeTakenSeconds: Int
)
