package com.mathstack.practice.application

import com.mathstack.academic.domain.repository.AcademicRepository
import com.mathstack.practice.domain.repository.PracticeRepository
import com.mathstack.users.domain.repository.UserProficiencyRepository
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
    private val academicRepository: AcademicRepository,
    private val userProficiencyRepository: UserProficiencyRepository
) {
    operator fun invoke(userId: UUID): List<LearningPathResponseDto> {
        val subjects = academicRepository.listSubjects()
        if (subjects.isEmpty()) return emptyList()

        val proficiencies = userProficiencyRepository.getAllProficiencies(userId)
        val sortedSubjects = subjects.sortedBy { proficiencies[it.id] ?: 0 }

        val userPaths = practiceRepository.findLearningPathsByUserId(userId)
        val userPathMap = userPaths.associateBy { it.lessonId }

        val response = mutableListOf<LearningPathResponseDto>()

        for (subject in sortedSubjects) {
            val allLessons = academicRepository.listLessonsBySubject(subject.id)
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

            response.add(
                LearningPathResponseDto(
                    subjectId = subject.id,
                    subjectName = subject.name,
                    lessons = lessonsResponse
                )
            )
        }

        return response
    }
}
