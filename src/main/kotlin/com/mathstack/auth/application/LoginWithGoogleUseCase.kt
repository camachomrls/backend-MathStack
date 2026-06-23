package com.mathstack.auth.application

import com.mathstack.auth.domain.model.AuthSession
import com.mathstack.auth.domain.repository.TokenService
import com.mathstack.users.domain.model.User
import com.mathstack.users.domain.repository.UserRepository
import java.time.LocalDateTime
import java.util.UUID

class LoginWithGoogleUseCase(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {
    operator fun invoke(command: LoginWithGoogleCommand): AuthSession {
        var user = command.firebaseUid?.let { userRepository.findUserByFirebaseUid(it) }
            ?: userRepository.findUserByEmail(command.email)

        if (user == null) {
            val newUser = User(
                id = UUID.randomUUID(),
                email = command.email.trim().lowercase(),
                username = command.username.trim(),
                passwordHash = "",
                firebaseUid = command.firebaseUid?.trim() ?: "",
                accessLevel = "STUDENT",
                createdAt = LocalDateTime.now()
            )
            user = userRepository.createUser(newUser)
        }

        return AuthSession(
            token = tokenService.generate(user.id, user.email, user.accessLevel),
            user = user
        )
    }
}

data class LoginWithGoogleCommand(
    val email: String,
    val username: String,
    val firebaseUid: String?
)
