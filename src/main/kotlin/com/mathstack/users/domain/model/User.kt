package com.mathstack.users.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val firebaseUid: String,
    val email: String,
    val username: String,
    val passwordHash: String,
    val accessLevel: String,
    val createdAt: LocalDateTime,
)
