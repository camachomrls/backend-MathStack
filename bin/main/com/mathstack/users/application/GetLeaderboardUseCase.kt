package com.mathstack.users.application

import com.mathstack.users.domain.model.UserProfile
import com.mathstack.users.domain.repository.UserRepository

class GetLeaderboardUseCase(private val userRepository: UserRepository) {
    operator fun invoke(limit: Int = 50): List<UserProfile> =
        userRepository.getLeaderboard(limit)
}
