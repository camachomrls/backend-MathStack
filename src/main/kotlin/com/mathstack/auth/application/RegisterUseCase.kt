package com.mathstack.auth.application

import com.mathstack.auth.domain.model.AuthSession
import com.mathstack.auth.domain.repository.PasswordHasher
import com.mathstack.auth.domain.repository.TokenService
import com.mathstack.shared.domain.exception.ConflictException
import com.mathstack.users.domain.model.User
import com.mathstack.users.domain.model.UserGamificationStats
import com.mathstack.users.domain.repository.UserRepository
import java.time.LocalDateTime
import java.util.UUID

class RegisterUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService,
) {
    operator fun invoke(command: RegisterCommand): AuthSession {
        if (userRepository.findUserByEmail(command.email) != null) {
            throw ConflictException("email is already registered")
        }

        val firebaseUid = command.firebaseUid ?: "local:${command.email}"
        if (userRepository.findUserByFirebaseUid(firebaseUid) != null) {
            throw ConflictException("firebaseUid is already registered")
        }

        val user = User(
            id = UUID.randomUUID(),
            firebaseUid = firebaseUid,
            email = command.email,
            username = command.username,
            passwordHash = passwordHasher.hash(command.password),
            accessLevel = command.accessLevel,
            createdAt = LocalDateTime.now(),
        )

        val createdUser = userRepository.createUser(user)
        if (userRepository.findStatsByUserId(createdUser.id) == null) {
            userRepository.createStats(UserGamificationStats(createdUser.id))
        }

        return AuthSession(
            token = tokenService.generate(createdUser.id, createdUser.email, createdUser.accessLevel),
            user = createdUser,
        )
    }
}

data class RegisterCommand(
    val email: String,
    val username: String,
    val password: String,
    val firebaseUid: String? = null,
    val accessLevel: String = "STUDENT",
)
