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
}
