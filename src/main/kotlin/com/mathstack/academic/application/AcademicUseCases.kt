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
                content = command.content?.trim(),
            ),
        )
    }
}

class GetLessonsBySubjectUseCase(
    private val repository: AcademicRepository,
    private val userProficiencyRepository: com.mathstack.users.domain.repository.UserProficiencyRepository
) {
    operator fun invoke(subjectId: Int, userId: UUID): List<Lesson> {
        repository.findSubjectById(subjectId)
            ?: throw NotFoundException("Subject $subjectId was not found")
        val lessons = repository.listLessonsBySubject(subjectId)
        val proficiency = userProficiencyRepository.getProficiency(userId, subjectId)
        
        return if (proficiency != null) {
            lessons.filter { it.difficultyLevel <= proficiency }
        } else {
            lessons
        }
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

class GetLessonByIdUseCase(private val repository: AcademicRepository) {
    operator fun invoke(id: UUID): Lesson {
        return repository.findLessonById(id)
            ?: throw NotFoundException("Lesson $id was not found")
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
    val content: String? = null,
)

data class CreateExerciseCommand(
    val lessonId: UUID,
    val content: String,
    val conceptTested: String?,
)

class UpdateLessonUseCase(private val repository: AcademicRepository) {
    operator fun invoke(id: UUID, command: UpdateLessonCommand): Lesson {
        val existingLesson = repository.findLessonById(id)
            ?: throw NotFoundException("Lesson $id was not found")

        command.subjectId?.let {
            repository.findSubjectById(it) ?: throw NotFoundException("Subject $it was not found")
        }
        command.lessonTypeId?.let {
            repository.findLessonTypeById(it) ?: throw NotFoundException("Lesson type $it was not found")
        }

        val updatedLesson = existingLesson.copy(
            subjectId = command.subjectId ?: existingLesson.subjectId,
            lessonTypeId = command.lessonTypeId ?: existingLesson.lessonTypeId,
            title = command.title?.trim() ?: existingLesson.title,
            difficultyLevel = command.difficultyLevel ?: existingLesson.difficultyLevel,
            content = if (command.content != null) command.content.trim() else existingLesson.content
        )

        return repository.updateLesson(updatedLesson)
            ?: throw NotFoundException("Lesson $id was not found")
    }
}

class UpdateExerciseUseCase(private val repository: AcademicRepository) {
    operator fun invoke(id: UUID, command: UpdateExerciseCommand): Exercise {
        val existingExercise = repository.findExerciseById(id)
            ?: throw NotFoundException("Exercise $id was not found")

        command.lessonId?.let {
            repository.findLessonById(it) ?: throw NotFoundException("Lesson $it was not found")
        }

        val updatedExercise = existingExercise.copy(
            lessonId = command.lessonId ?: existingExercise.lessonId,
            content = command.content?.trim() ?: existingExercise.content,
            conceptTested = if (command.conceptTested != null) command.conceptTested.trim() else existingExercise.conceptTested
        )

        return repository.updateExercise(updatedExercise)
            ?: throw NotFoundException("Exercise $id was not found")
    }
}

data class UpdateLessonCommand(
    val subjectId: Int? = null,
    val lessonTypeId: Int? = null,
    val title: String? = null,
    val difficultyLevel: Int? = null,
    val content: String? = null,
)

data class UpdateExerciseCommand(
    val lessonId: UUID? = null,
    val content: String? = null,
    val conceptTested: String? = null,
)
