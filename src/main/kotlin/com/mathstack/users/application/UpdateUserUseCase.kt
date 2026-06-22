package com.mathstack.users.application

import com.mathstack.shared.domain.exception.ConflictException
import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.users.domain.model.User
import com.mathstack.users.domain.repository.UserRepository
import java.util.UUID

class UpdateUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(userId: UUID, command: UpdateUserCommand): User {
        val currentUser = userRepository.findUserById(userId)
            ?: throw NotFoundException("User $userId was not found")

        val nextEmail = command.email ?: currentUser.email
        val userWithEmail = userRepository.findUserByEmail(nextEmail)
        if (userWithEmail != null && userWithEmail.id != userId) {
            throw ConflictException("email is already registered")
        }

        val updatedUser = currentUser.copy(
            email = nextEmail,
            username = command.username ?: currentUser.username,
        )

        return userRepository.updateUser(updatedUser)
            ?: throw NotFoundException("User $userId was not found")
    }
}

data class UpdateUserCommand(
    val email: String? = null,
    val username: String? = null,
)
