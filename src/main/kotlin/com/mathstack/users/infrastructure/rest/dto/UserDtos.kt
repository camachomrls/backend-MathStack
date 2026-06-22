package com.mathstack.users.infrastructure.rest.dto

import com.mathstack.shared.domain.exception.ValidationException
import com.mathstack.users.application.CreateUserCommand
import com.mathstack.users.application.UpdateGamificationStatsCommand
import com.mathstack.users.application.UpdateUserCommand
import com.mathstack.users.domain.model.User
import com.mathstack.users.domain.model.UserGamificationStats
import com.mathstack.users.domain.model.UserProfile
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val firebaseUid: String,
    val email: String,
    val username: String,
)

@Serializable
data class UpdateUserRequest(
    val email: String? = null,
    val username: String? = null,
)

@Serializable
data class UpdateGamificationStatsRequest(
    val coins: Int? = null,
    val currentLevel: Int? = null,
    val xpPoints: Int? = null,
    val lessonsCompletedCount: Int? = null,
    val currentStreak: Int? = null,
    val maxStreak: Int? = null,
    val minutesPracticed: Int? = null,
    val lastPracticeDate: String? = null,
)

@Serializable
data class UserResponse(
    val id: String,
    val firebaseUid: String,
    val email: String,
    val username: String,
    val accessLevel: String,
    val createdAt: String,
)

@Serializable
data class UserGamificationStatsResponse(
    val userId: String,
    val coins: Int,
    val currentLevel: Int,
    val xpPoints: Int,
    val lessonsCompletedCount: Int,
    val currentStreak: Int,
    val maxStreak: Int,
    val minutesPracticed: Int,
    val lastPracticeDate: String?,
)

@Serializable
data class UserProfileResponse(
    val user: UserResponse,
    val gamificationStats: UserGamificationStatsResponse,
)

fun CreateUserRequest.toCommand(): CreateUserCommand {
    validate()
    return CreateUserCommand(
        firebaseUid = firebaseUid.trim(),
        email = email.normalizedEmail(),
        username = username.trim(),
    )
}

fun UpdateUserRequest.toCommand(): UpdateUserCommand {
    validate()
    return UpdateUserCommand(
        email = email?.normalizedEmail(),
        username = username?.trim(),
    )
}

fun UpdateGamificationStatsRequest.toCommand(): UpdateGamificationStatsCommand {
    validate()
    return UpdateGamificationStatsCommand(
        coins = coins,
        currentLevel = currentLevel,
        xpPoints = xpPoints,
        lessonsCompletedCount = lessonsCompletedCount,
        currentStreak = currentStreak,
        maxStreak = maxStreak,
        minutesPracticed = minutesPracticed,
        lastPracticeDate = lastPracticeDate?.parseIsoDate("lastPracticeDate"),
    )
}

fun User.toResponse(): UserResponse =
    UserResponse(
        id = id.toString(),
        firebaseUid = firebaseUid,
        email = email,
        username = username,
        accessLevel = accessLevel,
        createdAt = createdAt.toString(),
    )

fun UserGamificationStats.toResponse(): UserGamificationStatsResponse =
    UserGamificationStatsResponse(
        userId = userId.toString(),
        coins = coins,
        currentLevel = currentLevel,
        xpPoints = xpPoints,
        lessonsCompletedCount = lessonsCompletedCount,
        currentStreak = currentStreak,
        maxStreak = maxStreak,
        minutesPracticed = minutesPracticed,
        lastPracticeDate = lastPracticeDate?.toString(),
    )

fun UserProfile.toResponse(): UserProfileResponse =
    UserProfileResponse(
        user = user.toResponse(),
        gamificationStats = gamificationStats.toResponse(),
    )

private fun CreateUserRequest.validate() {
    if (firebaseUid.isBlank()) {
        throw ValidationException("firebaseUid is required")
    }
    validateEmail(email)
    validateUsername(username)
}

private fun UpdateUserRequest.validate() {
    if (email == null && username == null) {
        throw ValidationException("At least one user field is required")
    }
    email?.let(::validateEmail)
    username?.let(::validateUsername)
}

private fun UpdateGamificationStatsRequest.validate() {
    if (
        coins == null &&
        currentLevel == null &&
        xpPoints == null &&
        lessonsCompletedCount == null &&
        currentStreak == null &&
        maxStreak == null &&
        minutesPracticed == null &&
        lastPracticeDate == null
    ) {
        throw ValidationException("At least one gamification stats field is required")
    }

    validateNonNegative("coins", coins)
    validateMinimum("currentLevel", currentLevel, 1)
    validateNonNegative("xpPoints", xpPoints)
    validateNonNegative("lessonsCompletedCount", lessonsCompletedCount)
    validateNonNegative("currentStreak", currentStreak)
    validateNonNegative("maxStreak", maxStreak)
    validateNonNegative("minutesPracticed", minutesPracticed)
    lastPracticeDate?.parseIsoDate("lastPracticeDate")
}

private fun validateEmail(value: String) {
    val normalized = value.normalizedEmail()
    if (!EmailRegex.matches(normalized)) {
        throw ValidationException("email must be valid")
    }
}

private fun validateUsername(value: String) {
    val normalized = value.trim()
    if (normalized.length !in 3..50) {
        throw ValidationException("username must contain between 3 and 50 characters")
    }
}

private fun validateNonNegative(field: String, value: Int?) {
    validateMinimum(field, value, 0)
}

private fun validateMinimum(field: String, value: Int?, minimum: Int) {
    if (value != null && value < minimum) {
        throw ValidationException("$field must be greater than or equal to $minimum")
    }
}

private fun String.normalizedEmail(): String =
    trim().lowercase()

private fun String.parseIsoDate(field: String): LocalDate =
    try {
        LocalDate.parse(this)
    } catch (_: DateTimeParseException) {
        throw ValidationException("$field must use ISO-8601 date format yyyy-MM-dd")
    }

private val EmailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
