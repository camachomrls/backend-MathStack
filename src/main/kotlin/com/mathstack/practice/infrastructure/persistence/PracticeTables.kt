package com.mathstack.practice.infrastructure.persistence

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object DiagnosticResultsTable : Table("diagnostic_results") {
    val id = uuid("id")
    val userId = uuid("user_id")
    val subjectId = integer("subject_id")
    val deficiencyScore = integer("deficiency_score")
    val evaluatedAt = datetime("evaluated_at")
    
    override val primaryKey = PrimaryKey(id)
}

object LearningPathsTable : Table("learning_paths") {
    val userId = uuid("user_id")
    val lessonId = uuid("lesson_id")
    val status = varchar("status", 50)
    val completedAt = datetime("completed_at").nullable()
    
    override val primaryKey = PrimaryKey(userId, lessonId)
}

object PracticeSessionsTable : Table("practice_sessions") {
    val userId = uuid("user_id")
    val sessionDate = date("session_date")
    val minutesSpent = integer("minutes_spent")
    
    override val primaryKey = PrimaryKey(userId, sessionDate)
}

object ExerciseAttemptsTable : Table("exercise_attempts") {
    val id = uuid("id")
    val userId = uuid("user_id")
    val exerciseId = uuid("exercise_id")
    val isCorrect = bool("is_correct")
    val attemptedAt = datetime("attempted_at")
    
    override val primaryKey = PrimaryKey(id)
}
