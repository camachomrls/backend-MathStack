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

class LogPracticeSessionUseCase(
    private val repository: PracticeRepository,
    private val userRepository: com.mathstack.users.domain.repository.UserRepository
) {
    operator fun invoke(command: LogPracticeSessionCommand): PracticeSession {
        val existingSession = repository.getPracticeSession(command.userId, command.sessionDate)
        val session = if (existingSession != null) {
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
        
        val stats = userRepository.findStatsByUserId(command.userId)
        if (stats != null) {
            val lastDate = stats.lastPracticeDate
            val today = LocalDate.now()
            
            var newStreak = stats.currentStreak
            if (lastDate == today.minusDays(1)) {
                newStreak += 1
            } else if (lastDate == null || lastDate.isBefore(today.minusDays(1))) {
                newStreak = 1
            }
            // if lastDate == today, streak remains the same
            
            val newMaxStreak = maxOf(stats.maxStreak, newStreak)
            val updatedStats = stats.copy(
                currentStreak = newStreak,
                maxStreak = newMaxStreak,
                lastPracticeDate = today,
                minutesPracticed = stats.minutesPracticed + command.minutesSpent
            )
            userRepository.updateStats(updatedStats)
        }

        return session
    }
}

class SubmitDiagnosticAnswersUseCase(
    private val practiceRepository: PracticeRepository,
    private val academicRepository: com.mathstack.academic.domain.repository.AcademicRepository
) {
    operator fun invoke(command: SubmitDiagnosticAnswersCommand): com.mathstack.practice.domain.model.DiagnosticResult {
        // Calculate deficiency score based on some logic (e.g., using command.score or average metric)
        val calculatedDeficiencyScore = 100 - command.score

        val diagnosticResult = practiceRepository.createDiagnosticResult(
            com.mathstack.practice.domain.model.DiagnosticResult(
                id = UUID.randomUUID(),
                userId = command.userId,
                subjectId = command.subjectId,
                deficiencyScore = calculatedDeficiencyScore,
                evaluatedAt = LocalDateTime.now()
            )
        )

        // Fetch all lessons for this subject
        val lessons = academicRepository.listLessonsBySubject(command.subjectId)
        
        // Populate learning paths based on results
        lessons.forEach { lesson ->
            val status = if (calculatedDeficiencyScore > 50) "pending" else "in_progress"
            practiceRepository.createLearningPath(
                com.mathstack.practice.domain.model.LearningPath(
                    userId = command.userId,
                    lessonId = lesson.id,
                    statusId = status,
                    completedAt = null
                )
            )
        }
        
        return diagnosticResult
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

data class SubmitDiagnosticAnswersCommand(
    val userId: UUID,
    val subjectId: Int,
    val score: Int
)
