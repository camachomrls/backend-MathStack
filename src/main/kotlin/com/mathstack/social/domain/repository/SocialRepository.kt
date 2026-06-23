package com.mathstack.social.domain.repository

import com.mathstack.social.domain.model.Challenge
import com.mathstack.social.domain.model.ChallengeParticipant
import com.mathstack.social.domain.model.FriendRequest
import java.util.UUID

interface SocialRepository {
    fun sendFriendRequest(request: FriendRequest): FriendRequest
    fun getFriendRequest(userId: UUID, friendId: UUID): FriendRequest?
    fun updateFriendRequest(request: FriendRequest): FriendRequest
    fun listFriends(userId: UUID): List<UUID>

    fun createChallenge(challenge: Challenge): Challenge
    fun getChallenge(id: UUID): Challenge?
    fun updateChallenge(challenge: Challenge): Challenge

    fun addChallengeParticipant(participant: ChallengeParticipant): ChallengeParticipant
    fun updateChallengeParticipant(participant: ChallengeParticipant): ChallengeParticipant
    fun getChallengeParticipants(challengeId: UUID): List<ChallengeParticipant>
    fun getChallengeParticipant(challengeId: UUID, userId: UUID): ChallengeParticipant?
}
