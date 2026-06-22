package com.mathstack.academic.domain.model

import java.util.UUID

data class Subject(val id: Int, val name: String)

data class LessonType(val id: Int, val name: String)

data class Lesson(
    val id: UUID,
    val subjectId: Int,
    val lessonTypeId: Int,
    val title: String,
    val difficultyLevel: Int,
)

data class Exercise(
    val id: UUID,
    val lessonId: UUID,
    val content: String,
    val conceptTested: String?,
)
