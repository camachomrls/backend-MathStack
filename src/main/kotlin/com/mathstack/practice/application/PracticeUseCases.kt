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
    private val academicRepository: com.mathstack.academic.domain.repository.AcademicRepository,
    private val userProficiencyRepository: com.mathstack.users.domain.repository.UserProficiencyRepository,
    private val userRepository: com.mathstack.users.domain.repository.UserRepository
) {
    operator fun invoke(command: SubmitDiagnosticAnswersCommand): List<DiagnosticSubjectScore> {
        val subjectScores = mutableMapOf<Int, Pair<Int, Int>>() // subjectId -> (correct, total)

        command.answers.forEach { answer ->
            val exercise = academicRepository.findExerciseById(answer.exerciseId) ?: return@forEach
            val lesson = academicRepository.findLessonById(exercise.lessonId) ?: return@forEach
            val current = subjectScores.getOrDefault(lesson.subjectId, Pair(0, 0))
            subjectScores[lesson.subjectId] = Pair(
                current.first + if (answer.isCorrect) 1 else 0,
                current.second + 1
            )
        }

        subjectScores.forEach { (subjectId, score) ->
            val percentage = if (score.second > 0) (score.first.toDouble() / score.second) * 100 else 0.0
            val level = when {
                percentage >= 80 -> 3 
                percentage >= 50 -> 2 
                else -> 1
            }
            userProficiencyRepository.saveProficiency(command.userId, subjectId, level)
        }

        val stats = userRepository.findStatsByUserId(command.userId)
        if (stats != null) {
            userRepository.updateStats(stats.copy(lastDiagnosticDate = LocalDateTime.now()))
        }

        return subjectScores.map { (subjectId, score) ->
            val percentage = if (score.second > 0) (score.first.toDouble() / score.second) * 100 else 0.0
            DiagnosticSubjectScore(subjectId, percentage)
        }
    }
}

class CompleteLessonUseCase(private val repository: PracticeRepository) {
    operator fun invoke(userId: UUID, lessonId: UUID) {
        repository.updateLearningPathStatus(userId, lessonId, "completed", LocalDateTime.now())
    }
}

data class DiagnosticSubjectScore(
    val subjectId: Int,
    val scorePercentage: Double
)

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

data class DiagnosticAnswer(
    val exerciseId: UUID,
    val isCorrect: Boolean
)

data class SubmitDiagnosticAnswersCommand(
    val userId: UUID,
    val answers: List<DiagnosticAnswer>
)
