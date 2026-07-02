package com.mathstack.practice.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class DiagnosticResult(
    val id: UUID,
    val userId: UUID,
    val subjectId: Int,
    val deficiencyScore: Int,
    val evaluatedAt: LocalDateTime,
)

data class LearningPath(
    val userId: UUID,
    val lessonId: UUID,
    val status: String, // 'pending', 'in_progress', 'completed'
    val completedAt: LocalDateTime?,
)

data class PracticeSession(
    val userId: UUID,
    val sessionDate: LocalDate,
    val minutesSpent: Int,
)

data class ExerciseAttempt(
    val id: UUID,
    val userId: UUID,
    val exerciseId: UUID,
    val isCorrect: Boolean,
    val attemptedAt: LocalDateTime,
)

data class StudentDashboardMetrics(
    val weeklyMinutesSpent: Int,
    val pendingLessonsCount: Int,
)
