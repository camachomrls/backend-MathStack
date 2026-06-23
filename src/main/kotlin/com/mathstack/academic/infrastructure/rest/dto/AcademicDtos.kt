package com.mathstack.academic.infrastructure.rest.dto

import com.mathstack.academic.application.CreateExerciseCommand
import com.mathstack.academic.application.CreateLessonCommand
import com.mathstack.academic.domain.model.Exercise
import com.mathstack.academic.domain.model.Lesson
import com.mathstack.academic.domain.model.LessonType
import com.mathstack.academic.domain.model.Subject
import com.mathstack.shared.domain.exception.ValidationException
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable data class CreateSubjectRequest(val name: String)
@Serializable data class CreateLessonTypeRequest(val name: String)
/**
 * Request to create a new Lesson.
 * @param title The title of the lesson. Can contain TeX (LaTeX) syntax for mathematical representation.
 * Remember to escape backslashes in JSON payloads (e.g., \\sqrt{x}).
 */
@Serializable data class CreateLessonRequest(val subjectId: Int, val lessonTypeId: Int, val title: String, val difficultyLevel: Int)

/**
 * Request to create a new Exercise.
 * @param content The exercise content or question text. Expects TeX (LaTeX) syntax for mathematical formulas.
 * Remember to escape backslashes in JSON payloads (e.g., \\int_0^1 x^2 dx).
 */
@Serializable data class CreateExerciseRequest(val lessonId: String, val content: String, val conceptTested: String? = null)

@Serializable data class SubjectResponse(val id: Int, val name: String)
@Serializable data class LessonTypeResponse(val id: Int, val name: String)
@Serializable data class LessonResponse(val id: String, val subjectId: Int, val lessonTypeId: Int, val title: String, val difficultyLevel: Int)
@Serializable data class ExerciseResponse(val id: String, val lessonId: String, val content: String, val conceptTested: String?)

fun CreateSubjectRequest.validName(): String {
    if (name.trim().length !in 2..100) throw ValidationException("name must contain between 2 and 100 characters")
    return name.trim()
}

fun CreateLessonTypeRequest.validName(): String {
    if (name.trim().length !in 2..50) throw ValidationException("name must contain between 2 and 50 characters")
    return name.trim()
}

fun CreateLessonRequest.toCommand(): CreateLessonCommand {
    if (subjectId <= 0) throw ValidationException("subjectId must be positive")
    if (lessonTypeId <= 0) throw ValidationException("lessonTypeId must be positive")
    if (title.trim().length !in 3..200) throw ValidationException("title must contain between 3 and 200 characters")
    if (difficultyLevel !in 1..10) throw ValidationException("difficultyLevel must be between 1 and 10")
    return CreateLessonCommand(subjectId, lessonTypeId, title.trim(), difficultyLevel)
}

fun CreateExerciseRequest.toCommand(): CreateExerciseCommand {
    if (content.trim().isBlank()) throw ValidationException("content is required")
    return CreateExerciseCommand(lessonId.toUuid("lessonId"), content.trim(), conceptTested?.trim())
}

fun Subject.toResponse(): SubjectResponse = SubjectResponse(id, name)
fun LessonType.toResponse(): LessonTypeResponse = LessonTypeResponse(id, name)
fun Lesson.toResponse(): LessonResponse = LessonResponse(id.toString(), subjectId, lessonTypeId, title, difficultyLevel)
fun Exercise.toResponse(): ExerciseResponse = ExerciseResponse(id.toString(), lessonId.toString(), content, conceptTested)

fun String.toUuid(field: String): UUID =
    runCatching { UUID.fromString(this) }
        .getOrElse { throw ValidationException("$field must be a valid UUID") }
