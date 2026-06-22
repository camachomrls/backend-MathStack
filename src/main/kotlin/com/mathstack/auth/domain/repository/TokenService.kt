package com.mathstack.auth.domain.repository

import java.util.UUID

interface TokenService {
    fun generate(userId: UUID, email: String, accessLevel: String): String
}
