package com.mathstack.academic.application

import com.mathstack.academic.domain.model.Exercise
import com.mathstack.academic.domain.model.Lesson
import com.mathstack.academic.domain.model.LessonType
import com.mathstack.academic.domain.model.Subject
import com.mathstack.academic.domain.repository.AcademicRepository
import com.mathstack.shared.domain.exception.NotFoundException
import java.util.UUID

class CreateSubjectUseCase(private val repository: AcademicRepository) {
    operator fun invoke(name: String): Subject = repository.createSubject(name.trim())
}

class ListSubjectsUseCase(private val repository: AcademicRepository) {
    operator fun invoke(): List<Subject> = repository.listSubjects()
}

class CreateLessonTypeUseCase(private val repository: AcademicRepository) {
    operator fun invoke(name: String): LessonType = repository.createLessonType(name.trim())
}

class ListLessonTypesUseCase(private val repository: AcademicRepository) {
    operator fun invoke(): List<LessonType> = repository.listLessonTypes()
}

class CreateLessonUseCase(private val repository: AcademicRepository) {
    operator fun invoke(command: CreateLessonCommand): Lesson {
        repository.findSubjectById(command.subjectId)
            ?: throw NotFoundException("Subject ${command.subjectId} was not found")
        repository.findLessonTypeById(command.lessonTypeId)
            ?: throw NotFoundException("Lesson type ${command.lessonTypeId} was not found")

        return repository.createLesson(
            Lesson(
                id = UUID.randomUUID(),
                subjectId = command.subjectId,
                lessonTypeId = command.lessonTypeId,
                title = command.title.trim(),
                difficultyLevel = command.difficultyLevel,
            ),
        )
    }
}

class GetLessonsBySubjectUseCase(private val repository: AcademicRepository) {
    operator fun invoke(subjectId: Int): List<Lesson> {
        repository.findSubjectById(subjectId)
            ?: throw NotFoundException("Subject $subjectId was not found")
        return repository.listLessonsBySubject(subjectId)
    }
}

class CreateExerciseUseCase(private val repository: AcademicRepository) {
    operator fun invoke(command: CreateExerciseCommand): Exercise {
        repository.findLessonById(command.lessonId)
            ?: throw NotFoundException("Lesson ${command.lessonId} was not found")

        return repository.createExercise(
            Exercise(
                id = UUID.randomUUID(),
                lessonId = command.lessonId,
                content = command.content.trim(),
                conceptTested = command.conceptTested?.trim(),
            ),
        )
    }
}

class GetExercisesByLessonUseCase(private val repository: AcademicRepository) {
    operator fun invoke(lessonId: UUID): List<Exercise> {
        repository.findLessonById(lessonId)
            ?: throw NotFoundException("Lesson $lessonId was not found")
        return repository.listExercisesByLesson(lessonId)
    }
}

class DeleteLessonUseCase(private val repository: AcademicRepository) {
    operator fun invoke(id: UUID) {
        if (!repository.deleteLesson(id)) throw NotFoundException("Lesson $id was not found")
    }
}

class DeleteExerciseUseCase(private val repository: AcademicRepository) {
    operator fun invoke(id: UUID) {
        if (!repository.deleteExercise(id)) throw NotFoundException("Exercise $id was not found")
    }
}

data class CreateLessonCommand(
    val subjectId: Int,
    val lessonTypeId: Int,
    val title: String,
    val difficultyLevel: Int,
)

data class CreateExerciseCommand(
    val lessonId: UUID,
    val content: String,
    val conceptTested: String?,
)
