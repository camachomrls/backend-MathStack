package com.mathstack.practice.application

import com.mathstack.practice.domain.model.ExerciseAttempt
import com.mathstack.practice.domain.model.PracticeSession
import com.mathstack.practice.domain.model.StudentDashboardMetrics
import com.mathstack.practice.domain.repository.PracticeRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class RegisterExerciseAttemptUseCase(private val repository: PracticeRepository) {
    operator fun invoke(command: RegisterExerciseAttemptCommand): ExerciseAttempt {
        return repository.createExerciseAttempt(
            ExerciseAttempt(
                id = UUID.randomUUID(),
                userId = command.userId,
                exerciseId = command.exerciseId,
                isCorrect = command.isCorrect,
                attemptedAt = LocalDateTime.now(),
            )
        )
    }
}

class LogPracticeSessionUseCase(private val repository: PracticeRepository) {
    operator fun invoke(command: LogPracticeSessionCommand): PracticeSession {
        val existingSession = repository.getPracticeSession(command.userId, command.sessionDate)
        return if (existingSession != null) {
            repository.updatePracticeSession(
                existingSession.copy(minutesSpent = existingSession.minutesSpent + command.minutesSpent)
            )
        } else {
            repository.createPracticeSession(
                PracticeSession(
                    userId = command.userId,
                    sessionDate = command.sessionDate,
                    minutesSpent = command.minutesSpent,
                )
            )
        }
    }
}

class GetStudentDashboardMetricsUseCase(private val repository: PracticeRepository) {
    operator fun invoke(userId: UUID): StudentDashboardMetrics {
        val weeklyMinutes = repository.getWeeklyMinutesSpent(userId)
        val pendingLessons = repository.getPendingLessonsCount(userId)
        
        return StudentDashboardMetrics(
            weeklyMinutesSpent = weeklyMinutes,
            pendingLessonsCount = pendingLessons,
        )
    }
}

data class RegisterExerciseAttemptCommand(
    val userId: UUID,
    val exerciseId: UUID,
    val isCorrect: Boolean,
)

data class LogPracticeSessionCommand(
    val userId: UUID,
    val sessionDate: LocalDate,
    val minutesSpent: Int,
)
