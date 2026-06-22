package com.mathstack.users.application

import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.users.domain.model.UserGamificationStats
import com.mathstack.users.domain.repository.UserRepository
import java.time.LocalDate
import java.util.UUID

class UpdateGamificationStatsUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(userId: UUID, command: UpdateGamificationStatsCommand): UserGamificationStats {
        val currentStats = userRepository.findStatsByUserId(userId)
            ?: throw NotFoundException("Gamification stats for user $userId were not found")

        val nextStats = currentStats.copy(
            coins = command.coins ?: currentStats.coins,
            currentLevel = command.currentLevel ?: currentStats.currentLevel,
            xpPoints = command.xpPoints ?: currentStats.xpPoints,
            lessonsCompletedCount = command.lessonsCompletedCount
                ?: currentStats.lessonsCompletedCount,
            currentStreak = command.currentStreak ?: currentStats.currentStreak,
            maxStreak = command.maxStreak ?: currentStats.maxStreak,
            minutesPracticed = command.minutesPracticed ?: currentStats.minutesPracticed,
            lastPracticeDate = command.lastPracticeDate ?: currentStats.lastPracticeDate,
        )

        return userRepository.updateStats(nextStats)
            ?: throw NotFoundException("Gamification stats for user $userId were not found")
    }
}

data class UpdateGamificationStatsCommand(
    val coins: Int? = null,
    val currentLevel: Int? = null,
    val xpPoints: Int? = null,
    val lessonsCompletedCount: Int? = null,
    val currentStreak: Int? = null,
    val maxStreak: Int? = null,
    val minutesPracticed: Int? = null,
    val lastPracticeDate: LocalDate? = null,
)
