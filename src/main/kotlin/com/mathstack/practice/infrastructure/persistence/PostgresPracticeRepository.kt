package com.mathstack.practice.infrastructure.persistence

import com.mathstack.practice.domain.model.ExerciseAttempt
import com.mathstack.practice.domain.model.PracticeSession
import com.mathstack.practice.domain.repository.PracticeRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import java.util.UUID

class PostgresPracticeRepository : PracticeRepository {
    override fun createExerciseAttempt(attempt: ExerciseAttempt): ExerciseAttempt = transaction {
        ExerciseAttemptsTable.insert {
            it[id] = attempt.id
            it[userId] = attempt.userId
            it[exerciseId] = attempt.exerciseId
            it[isCorrect] = attempt.isCorrect
            it[attemptedAt] = attempt.attemptedAt
        }
        ExerciseAttemptsTable.selectAll().where { ExerciseAttemptsTable.id eq attempt.id }.single().toExerciseAttempt()
    }

    override fun getPracticeSession(userId: UUID, date: LocalDate): PracticeSession? = transaction {
        PracticeSessionsTable.selectAll()
            .where { (PracticeSessionsTable.userId eq userId) and (PracticeSessionsTable.sessionDate eq date) }
            .singleOrNull()?.toPracticeSession()
    }

    override fun createPracticeSession(session: PracticeSession): PracticeSession = transaction {
        PracticeSessionsTable.insert {
            it[userId] = session.userId
            it[sessionDate] = session.sessionDate
            it[minutesSpent] = session.minutesSpent
        }
        getPracticeSession(session.userId, session.sessionDate)!!
    }

    override fun updatePracticeSession(session: PracticeSession): PracticeSession = transaction {
        PracticeSessionsTable.update({
            (PracticeSessionsTable.userId eq session.userId) and (PracticeSessionsTable.sessionDate eq session.sessionDate)
        }) {
            it[minutesSpent] = session.minutesSpent
        }
        getPracticeSession(session.userId, session.sessionDate)!!
    }

    override fun getWeeklyMinutesSpent(userId: UUID): Int = transaction {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val sumColumn = PracticeSessionsTable.minutesSpent.sum()
        
        val result = PracticeSessionsTable
            .select(sumColumn)
            .where { 
                (PracticeSessionsTable.userId eq userId) and 
                (PracticeSessionsTable.sessionDate greaterEq startOfWeek) and 
                (PracticeSessionsTable.sessionDate lessEq endOfWeek) 
            }
            .singleOrNull()

        result?.getOrNull(sumColumn) ?: 0
    }

    override fun getPendingLessonsCount(userId: UUID): Int = transaction {
        LearningPathsTable.selectAll()
            .where { 
                (LearningPathsTable.userId eq userId) and 
                ((LearningPathsTable.status eq "pending") or (LearningPathsTable.status eq "in_progress")) 
            }
            .count().toInt()
    }

    override fun createDiagnosticResult(result: com.mathstack.practice.domain.model.DiagnosticResult): com.mathstack.practice.domain.model.DiagnosticResult = transaction {
        DiagnosticResultsTable.insert {
            it[id] = result.id
            it[userId] = result.userId
            it[subjectId] = result.subjectId
            it[deficiencyScore] = result.deficiencyScore
            it[evaluatedAt] = result.evaluatedAt
        }
        DiagnosticResultsTable.selectAll().where { DiagnosticResultsTable.id eq result.id }.single().toDiagnosticResult()
    }

    override fun createLearningPath(path: com.mathstack.practice.domain.model.LearningPath): com.mathstack.practice.domain.model.LearningPath = transaction {
        LearningPathsTable.insert {
            it[userId] = path.userId
            it[lessonId] = path.lessonId
            it[status] = path.status
            it[completedAt] = path.completedAt
        }
        LearningPathsTable.selectAll().where { (LearningPathsTable.userId eq path.userId) and (LearningPathsTable.lessonId eq path.lessonId) }.single().toLearningPath()
    }

    override fun findAllDiagnostics(): List<com.mathstack.practice.domain.model.DiagnosticResult> = transaction {
        DiagnosticResultsTable.selectAll().map { it.toDiagnosticResult() }
    }

    override fun findAllSessions(): List<PracticeSession> = transaction {
        PracticeSessionsTable.selectAll().map { it.toPracticeSession() }
    }
}
