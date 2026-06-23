package com.mathstack.admin.application

import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.users.domain.model.UserProfile
import com.mathstack.users.domain.repository.UserRepository
import java.util.UUID

class GetUserProfileUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: UUID): UserProfile {
        return userRepository.findProfileByUserId(userId)
            ?: throw NotFoundException("User profile not found")
    }
}
