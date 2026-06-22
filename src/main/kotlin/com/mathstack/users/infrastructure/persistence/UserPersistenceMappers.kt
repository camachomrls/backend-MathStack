package com.mathstack.users.infrastructure.persistence

import com.mathstack.users.domain.model.User
import com.mathstack.users.domain.model.UserGamificationStats
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser(): User =
    User(
        id = this[UserTable.id],
        firebaseUid = this[UserTable.firebaseUid],
        email = this[UserTable.email],
        username = this[UserTable.username],
        passwordHash = this[UserTable.passwordHash],
        accessLevel = this[UserTable.accessLevel],
        createdAt = this[UserTable.createdAt],
    )

fun ResultRow.toUserGamificationStats(): UserGamificationStats =
    UserGamificationStats(
        userId = this[UserGamificationStatsTable.userId],
        coins = this[UserGamificationStatsTable.coins],
        currentLevel = this[UserGamificationStatsTable.currentLevel],
        xpPoints = this[UserGamificationStatsTable.xpPoints],
        lessonsCompletedCount = this[UserGamificationStatsTable.lessonsCompletedCount],
        currentStreak = this[UserGamificationStatsTable.currentStreak],
        maxStreak = this[UserGamificationStatsTable.maxStreak],
        minutesPracticed = this[UserGamificationStatsTable.minutesPracticed],
        lastPracticeDate = this[UserGamificationStatsTable.lastPracticeDate],
    )
