package com.mathstack.practice.infrastructure.persistence

import com.mathstack.practice.domain.model.DiagnosticResult
import com.mathstack.practice.domain.model.ExerciseAttempt
import com.mathstack.practice.domain.model.LearningPath
import com.mathstack.practice.domain.model.PracticeSession
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toDiagnosticResult() = DiagnosticResult(
    id = this[DiagnosticResultsTable.id],
    userId = this[DiagnosticResultsTable.userId],
    subjectId = this[DiagnosticResultsTable.subjectId],
    deficiencyScore = this[DiagnosticResultsTable.deficiencyScore],
    evaluatedAt = this[DiagnosticResultsTable.evaluatedAt],
)

fun ResultRow.toLearningPath() = LearningPath(
    userId = this[LearningPathsTable.userId],
    lessonId = this[LearningPathsTable.lessonId],
    status = this[LearningPathsTable.status],
    completedAt = this[LearningPathsTable.completedAt],
)

fun ResultRow.toPracticeSession() = PracticeSession(
    userId = this[PracticeSessionsTable.userId],
    sessionDate = this[PracticeSessionsTable.sessionDate],
    minutesSpent = this[PracticeSessionsTable.minutesSpent],
)

fun ResultRow.toExerciseAttempt() = ExerciseAttempt(
    id = this[ExerciseAttemptsTable.id],
    userId = this[ExerciseAttemptsTable.userId],
    exerciseId = this[ExerciseAttemptsTable.exerciseId],
    isCorrect = this[ExerciseAttemptsTable.isCorrect],
    attemptedAt = this[ExerciseAttemptsTable.attemptedAt],
)
