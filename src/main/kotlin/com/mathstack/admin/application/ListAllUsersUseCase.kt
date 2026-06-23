package com.mathstack.admin.application

import com.mathstack.users.domain.model.User
import com.mathstack.users.domain.repository.UserRepository

class ListAllUsersUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): List<User> {
        return userRepository.findAll()
    }
}
