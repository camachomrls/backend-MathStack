package com.mathstack.practice.application

import com.mathstack.academic.domain.repository.AcademicRepository
import com.mathstack.practice.infrastructure.rest.dto.DiagnosticQuestionResponse

class GenerateDiagnosticQuizUseCase(
    private val academicRepository: AcademicRepository
) {
    operator fun invoke(): List<DiagnosticQuestionResponse> {
        val allExercises = academicRepository.listAllExercises()
        if (allExercises.isEmpty()) return emptyList()
        
        val randomExercises = allExercises.shuffled().take(30)
        
        // Fetch all subjects and lessons once to avoid N+1 queries
        val allSubjects = academicRepository.listSubjects().associateBy { it.id }
        val allLessons = academicRepository.listLessons().associateBy { it.id }
        
        return randomExercises.mapNotNull { exercise ->
            val lesson = allLessons[exercise.lessonId] ?: return@mapNotNull null
            val subject = allSubjects[lesson.subjectId] ?: return@mapNotNull null
            
            DiagnosticQuestionResponse(
                id = exercise.id.toString(),
                lessonId = exercise.lessonId.toString(),
                content = exercise.content,
                conceptTested = exercise.conceptTested,
                subjectId = subject.id,
                subjectName = subject.name
            )
        }
    }
}
