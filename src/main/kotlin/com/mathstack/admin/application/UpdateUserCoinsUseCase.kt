package com.mathstack.admin.application

import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.users.domain.model.UserGamificationStats
import com.mathstack.users.domain.repository.UserRepository
import java.util.UUID

class UpdateUserCoinsUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: UUID, coins: Int): UserGamificationStats {
        val stats = userRepository.findStatsByUserId(userId)
            ?: throw NotFoundException("Stats not found for user $userId")
        
        val updatedStats = stats.copy(coins = coins)
        return userRepository.updateStats(updatedStats)
            ?: throw NotFoundException("Could not update stats for user $userId")
    }
}
