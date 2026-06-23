package com.mathstack.practice.domain.repository

import com.mathstack.practice.domain.model.ExerciseAttempt
import com.mathstack.practice.domain.model.PracticeSession
import java.time.LocalDate
import java.util.UUID

interface PracticeRepository {
    fun createExerciseAttempt(attempt: ExerciseAttempt): ExerciseAttempt
    fun getPracticeSession(userId: UUID, date: LocalDate): PracticeSession?
    fun createPracticeSession(session: PracticeSession): PracticeSession
    fun updatePracticeSession(session: PracticeSession): PracticeSession
    fun getWeeklyMinutesSpent(userId: UUID): Int
    fun getPendingLessonsCount(userId: UUID): Int
    
    fun createDiagnosticResult(result: com.mathstack.practice.domain.model.DiagnosticResult): com.mathstack.practice.domain.model.DiagnosticResult
    fun createLearningPath(path: com.mathstack.practice.domain.model.LearningPath): com.mathstack.practice.domain.model.LearningPath
    
    fun findAllDiagnostics(): List<com.mathstack.practice.domain.model.DiagnosticResult>
    fun findAllSessions(): List<PracticeSession>
}
