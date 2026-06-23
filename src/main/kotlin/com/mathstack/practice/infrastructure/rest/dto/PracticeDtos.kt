package com.mathstack.practice.infrastructure.rest.dto

import com.mathstack.practice.domain.model.ExerciseAttempt
import com.mathstack.practice.domain.model.PracticeSession
import com.mathstack.practice.domain.model.StudentDashboardMetrics
import kotlinx.serialization.Serializable

@Serializable
data class RegisterExerciseAttemptRequest(
    val exerciseId: String,
    val isCorrect: Boolean,
)

@Serializable
data class ExerciseAttemptResponse(
    val id: String,
    val userId: String,
    val exerciseId: String,
    val isCorrect: Boolean,
    val attemptedAt: String,
)

@Serializable
data class LogPracticeSessionRequest(
    val sessionDate: String,
    val minutesSpent: Int,
)

@Serializable
data class PracticeSessionResponse(
    val userId: String,
    val sessionDate: String,
    val minutesSpent: Int,
)

@Serializable
data class StudentDashboardMetricsResponse(
    val weeklyMinutesSpent: Int,
    val pendingLessonsCount: Int,
)

fun ExerciseAttempt.toResponse() = ExerciseAttemptResponse(
    id = id.toString(),
    userId = userId.toString(),
    exerciseId = exerciseId.toString(),
    isCorrect = isCorrect,
    attemptedAt = attemptedAt.toString(),
)

fun PracticeSession.toResponse() = PracticeSessionResponse(
    userId = userId.toString(),
    sessionDate = sessionDate.toString(),
    minutesSpent = minutesSpent,
)

fun StudentDashboardMetrics.toResponse() = StudentDashboardMetricsResponse(
    weeklyMinutesSpent = weeklyMinutesSpent,
    pendingLessonsCount = pendingLessonsCount,
)

@Serializable
data class SubmitDiagnosticRequest(
    val subjectId: Int,
    val score: Int
)

@Serializable
data class DiagnosticResponse(
    val id: String,
    val userId: String,
    val subjectId: Int,
    val deficiencyScore: Int,
    val evaluatedAt: String,
)

fun com.mathstack.practice.domain.model.DiagnosticResult.toResponse() = DiagnosticResponse(
    id = id.toString(),
    userId = userId.toString(),
    subjectId = subjectId,
    deficiencyScore = deficiencyScore,
    evaluatedAt = evaluatedAt.toString()
)
