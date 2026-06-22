package com.mathstack.users.domain.model

import java.time.LocalDate
import java.util.UUID

data class UserGamificationStats(
    val userId: UUID,
    val coins: Int = 0,
    val currentLevel: Int = 1,
    val xpPoints: Int = 0,
    val lessonsCompletedCount: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val minutesPracticed: Int = 0,
    val lastPracticeDate: LocalDate? = null,
) {
    init {
        require(coins >= 0) { "coins must be greater than or equal to 0" }
        require(currentLevel >= 1) { "currentLevel must be greater than or equal to 1" }
        require(xpPoints >= 0) { "xpPoints must be greater than or equal to 0" }
        require(lessonsCompletedCount >= 0) {
            "lessonsCompletedCount must be greater than or equal to 0"
        }
        require(currentStreak >= 0) { "currentStreak must be greater than or equal to 0" }
        require(maxStreak >= 0) { "maxStreak must be greater than or equal to 0" }
        require(minutesPracticed >= 0) { "minutesPracticed must be greater than or equal to 0" }
    }
}
