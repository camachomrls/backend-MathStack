package com.mathstack.practice.application

import com.mathstack.academic.domain.model.Exercise
import com.mathstack.academic.domain.repository.AcademicRepository

class GenerateDiagnosticQuizUseCase(
    private val academicRepository: AcademicRepository
) {
    operator fun invoke(): List<Exercise> {
        val allExercises = academicRepository.listAllExercises()
        
        // Return 30 random exercises
        return allExercises.shuffled().take(30)
    }
}
