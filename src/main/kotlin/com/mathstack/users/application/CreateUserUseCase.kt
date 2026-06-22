package com.mathstack.users.application

import com.mathstack.shared.domain.exception.ConflictException
import com.mathstack.users.domain.model.User
import com.mathstack.users.domain.model.UserGamificationStats
import com.mathstack.users.domain.model.UserProfile
import com.mathstack.users.domain.repository.UserRepository
import java.time.LocalDateTime
import java.util.UUID

class CreateUserUseCase(
    private val userRepository: UserRepository,
) {
    operator fun invoke(command: CreateUserCommand): UserProfile {
        if (userRepository.findUserByFirebaseUid(command.firebaseUid) != null) {
            throw ConflictException("firebaseUid is already registered")
        }
        if (userRepository.findUserByEmail(command.email) != null) {
            throw ConflictException("email is already registered")
        }

        val user = User(
            id = UUID.randomUUID(),
            firebaseUid = command.firebaseUid,
            email = command.email,
            username = command.username,
            passwordHash = command.passwordHash,
            accessLevel = command.accessLevel,
            createdAt = LocalDateTime.now(),
        )

        val createdUser = userRepository.createUser(user)
        val stats = userRepository.findStatsByUserId(createdUser.id)
            ?: userRepository.createStats(UserGamificationStats(createdUser.id))

        return UserProfile(user = createdUser, gamificationStats = stats)
    }
}

data class CreateUserCommand(
    val firebaseUid: String,
    val email: String,
    val username: String,
    val passwordHash: String = "",
    val accessLevel: String = "STUDENT",
)
