package com.mathstack.users.infrastructure.persistence

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object UserGamificationStatsTable : Table("user_gamification_stats") {
    val userId = uuid("user_id").references(UserTable.id)
    val coins = integer("coins")
    val currentLevel = integer("current_level")
    val xpPoints = integer("xp_points")
    val lessonsCompletedCount = integer("lessons_completed_count")
    val currentStreak = integer("current_streak")
    val maxStreak = integer("max_streak")
    val minutesPracticed = integer("minutes_practiced")
    val lastPracticeDate = date("last_practice_date").nullable()

    override val primaryKey = PrimaryKey(userId)
}
