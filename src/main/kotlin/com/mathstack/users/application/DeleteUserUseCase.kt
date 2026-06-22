package com.mathstack.users.application

import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.users.domain.repository.UserRepository
import java.util.UUID

class DeleteUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(userId: UUID) {
        val deleted = userRepository.deleteUser(userId)
        if (!deleted) {
            throw NotFoundException("User $userId was not found")
        }
    }
}
