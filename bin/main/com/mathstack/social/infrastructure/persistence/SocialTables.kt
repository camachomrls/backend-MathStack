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

object GroupsTable : Table("groups") {
    val id = uuid("id")
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val subject = varchar("subject", 50)
    val maxMembers = integer("max_members")
    val creatorId = uuid("creator_id")
    val createdAt = datetime("created_at")
    val activeChallenges = integer("active_challenges").default(0)
    val totalXp = integer("total_xp").default(0)
    val color = varchar("color", 50).default("from-blue-500 to-blue-600")
    val activeLevelId = uuid("active_level_id").nullable()
    
    override val primaryKey = PrimaryKey(id)
}

object GroupMembersTable : Table("group_members") {
    val groupId = uuid("group_id")
    val userId = uuid("user_id")
    val role = varchar("role", 20)
    val joinedAt = datetime("joined_at")
    
    override val primaryKey = PrimaryKey(groupId, userId)
}
