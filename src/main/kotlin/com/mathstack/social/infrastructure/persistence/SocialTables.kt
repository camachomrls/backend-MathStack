package com.mathstack.social.infrastructure.persistence

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object FriendsTable : Table("friends") {
    val userId = uuid("user_id")
    val friendId = uuid("friend_id")
    val status = varchar("status", 50)
    
    override val primaryKey = PrimaryKey(userId, friendId)
}

object ChallengesTable : Table("challenges") {
    val id = uuid("id")
    val creatorId = uuid("creator_id")
    val exerciseId = uuid("exercise_id")
    val status = varchar("status", 50)
    
    override val primaryKey = PrimaryKey(id)
}

object ChallengeParticipantsTable : Table("challenge_participants") {
    val challengeId = uuid("challenge_id")
    val userId = uuid("user_id")
    val score = integer("score")
    val timeTakenSeconds = integer("time_taken_seconds")
    val completedAt = datetime("completed_at").nullable()
    
    override val primaryKey = PrimaryKey(challengeId, userId)
}
