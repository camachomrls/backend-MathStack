package com.mathstack.social.infrastructure.persistence

import com.mathstack.social.domain.model.Challenge
import com.mathstack.social.domain.model.ChallengeParticipant
import com.mathstack.social.domain.model.FriendRequest
import com.mathstack.social.domain.repository.SocialRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class PostgresSocialRepository : SocialRepository {
    override fun sendFriendRequest(request: FriendRequest): FriendRequest = transaction {
        FriendsTable.insert {
            it[userId] = request.userId
            it[friendId] = request.friendId
            it[status] = request.status
        }
        getFriendRequest(request.userId, request.friendId)!!
    }

    override fun getFriendRequest(userId: UUID, friendId: UUID): FriendRequest? = transaction {
        FriendsTable.selectAll().where { 
            ((FriendsTable.userId eq userId) and (FriendsTable.friendId eq friendId)) or
            ((FriendsTable.userId eq friendId) and (FriendsTable.friendId eq userId))
        }.firstOrNull()?.toFriendRequest()
    }

    override fun updateFriendRequest(request: FriendRequest): FriendRequest = transaction {
        FriendsTable.update({ 
            ((FriendsTable.userId eq request.userId) and (FriendsTable.friendId eq request.friendId)) or
            ((FriendsTable.userId eq request.friendId) and (FriendsTable.friendId eq request.userId))
        }) {
            it[status] = request.status
        }
        getFriendRequest(request.userId, request.friendId)!!
    }

    override fun listFriends(userId: UUID): List<UUID> = transaction {
        FriendsTable.selectAll().where { 
            ((FriendsTable.userId eq userId) or (FriendsTable.friendId eq userId)) and
            (FriendsTable.status eq "ACCEPTED")
        }.map { row ->
            val u1 = row[FriendsTable.userId]
            val u2 = row[FriendsTable.friendId]
            if (u1 == userId) u2 else u1
        }
    }

    override fun createChallenge(challenge: Challenge): Challenge = transaction {
        ChallengesTable.insert {
            it[id] = challenge.id
            it[creatorId] = challenge.creatorId
            it[exerciseId] = challenge.exerciseId
            it[status] = challenge.status
        }
        getChallenge(challenge.id)!!
    }

    override fun getChallenge(id: UUID): Challenge? = transaction {
        ChallengesTable.selectAll().where { ChallengesTable.id eq id }.singleOrNull()?.toChallenge()
    }

    override fun updateChallenge(challenge: Challenge): Challenge = transaction {
        ChallengesTable.update({ ChallengesTable.id eq challenge.id }) {
            it[status] = challenge.status
        }
        getChallenge(challenge.id)!!
    }

    override fun addChallengeParticipant(participant: ChallengeParticipant): ChallengeParticipant = transaction {
        ChallengeParticipantsTable.insert {
            it[challengeId] = participant.challengeId
            it[userId] = participant.userId
            it[score] = participant.score
            it[timeTakenSeconds] = participant.timeTakenSeconds
            it[completedAt] = participant.completedAt
        }
        getChallengeParticipant(participant.challengeId, participant.userId)!!
    }

    override fun updateChallengeParticipant(participant: ChallengeParticipant): ChallengeParticipant = transaction {
        ChallengeParticipantsTable.update({
            (ChallengeParticipantsTable.challengeId eq participant.challengeId) and
            (ChallengeParticipantsTable.userId eq participant.userId)
        }) {
            it[score] = participant.score
            it[timeTakenSeconds] = participant.timeTakenSeconds
            it[completedAt] = participant.completedAt
        }
        getChallengeParticipant(participant.challengeId, participant.userId)!!
    }

    override fun getChallengeParticipants(challengeId: UUID): List<ChallengeParticipant> = transaction {
        ChallengeParticipantsTable.selectAll().where { ChallengeParticipantsTable.challengeId eq challengeId }
            .map { it.toChallengeParticipant() }
    }

    override fun getChallengeParticipant(challengeId: UUID, userId: UUID): ChallengeParticipant? = transaction {
        ChallengeParticipantsTable.selectAll().where { 
            (ChallengeParticipantsTable.challengeId eq challengeId) and
            (ChallengeParticipantsTable.userId eq userId)
        }.singleOrNull()?.toChallengeParticipant()
    }
}

fun ResultRow.toFriendRequest() = FriendRequest(
    userId = this[FriendsTable.userId],
    friendId = this[FriendsTable.friendId],
    status = this[FriendsTable.status]
)

fun ResultRow.toChallenge() = Challenge(
    id = this[ChallengesTable.id],
    creatorId = this[ChallengesTable.creatorId],
    exerciseId = this[ChallengesTable.exerciseId],
    status = this[ChallengesTable.status]
)

fun ResultRow.toChallengeParticipant() = ChallengeParticipant(
    challengeId = this[ChallengeParticipantsTable.challengeId],
    userId = this[ChallengeParticipantsTable.userId],
    score = this[ChallengeParticipantsTable.score],
    timeTakenSeconds = this[ChallengeParticipantsTable.timeTakenSeconds],
    completedAt = this[ChallengeParticipantsTable.completedAt]
)
