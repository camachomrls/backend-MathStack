package com.mathstack.auth.domain.model

import com.mathstack.users.domain.model.User

data class AuthSession(
    val token: String,
    val user: User,
)
