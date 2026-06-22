package com.mathstack.auth.application

import com.mathstack.auth.domain.model.AuthSession
import com.mathstack.auth.domain.repository.PasswordHasher
import com.mathstack.auth.domain.repository.TokenService
import com.mathstack.shared.domain.exception.UnauthorizedException
import com.mathstack.users.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService,
) {
    operator fun invoke(command: LoginCommand): AuthSession {
        val user = userRepository.findUserByEmail(command.email)
            ?: throw UnauthorizedException("Invalid email or password")

        if (!passwordHasher.verify(command.password, user.passwordHash)) {
            throw UnauthorizedException("Invalid email or password")
        }

        return AuthSession(
            token = tokenService.generate(user.id, user.email, user.accessLevel),
            user = user,
        )
    }
}

data class LoginCommand(
    val email: String,
    val password: String,
)
