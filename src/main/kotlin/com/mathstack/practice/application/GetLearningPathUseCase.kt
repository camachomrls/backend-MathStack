package com.mathstack.practice.application

import com.mathstack.academic.domain.repository.AcademicRepository
import com.mathstack.practice.domain.repository.PracticeRepository
import java.util.UUID

data class LearningPathLessonDto(
    val id: String,
    val title: String,
    val difficultyLevel: Int,
    val xp: Int,
    val status: String
)

data class LearningPathResponseDto(
    val subjectId: Int,
    val subjectName: String,
    val lessons: List<LearningPathLessonDto>
)

class GetLearningPathUseCase(
    private val practiceRepository: PracticeRepository,
    private val academicRepository: AcademicRepository
) {
    operator fun invoke(userId: UUID): LearningPathResponseDto {
        val diagnostics = practiceRepository.findDiagnosticsByUserId(userId)
        
        val prioritySubjectId = diagnostics.maxByOrNull { it.deficiencyScore }?.subjectId ?: 1
        
        val subject = academicRepository.findSubjectById(prioritySubjectId) 
            ?: throw IllegalStateException("Subject not found for learning path")

        val allLessons = academicRepository.listLessonsBySubject(prioritySubjectId)
        
        val userPaths = practiceRepository.findLearningPathsByUserId(userId)
        val userPathMap = userPaths.associateBy { it.lessonId }

        val lessonsResponse = mutableListOf<LearningPathLessonDto>()
        var previousLessonCompleted = true 

        for (lesson in allLessons.sortedBy { it.difficultyLevel }) {
            val userPath = userPathMap[lesson.id]
            
            val status = when {
                userPath != null -> userPath.status
                previousLessonCompleted -> "available"
                else -> "locked"
            }

            lessonsResponse.add(
                LearningPathLessonDto(
                    id = lesson.id.toString(),
                    title = lesson.title,
                    difficultyLevel = lesson.difficultyLevel,
                    xp = lesson.difficultyLevel * 25,
                    status = status
                )
            )
            
            previousLessonCompleted = (status == "completed")
        }

        return LearningPathResponseDto(
            subjectId = subject.id,
            subjectName = subject.name,
            lessons = lessonsResponse
        )
    }
}
