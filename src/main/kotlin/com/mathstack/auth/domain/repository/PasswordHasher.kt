package com.mathstack.auth.domain.repository

interface PasswordHasher {
    fun hash(rawPassword: String): String
    fun verify(rawPassword: String, passwordHash: String): Boolean
}
