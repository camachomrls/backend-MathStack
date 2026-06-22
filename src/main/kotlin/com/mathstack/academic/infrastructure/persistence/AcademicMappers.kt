package com.mathstack.academic.infrastructure.persistence

import com.mathstack.academic.domain.model.Exercise
import com.mathstack.academic.domain.model.Lesson
import com.mathstack.academic.domain.model.LessonType
import com.mathstack.academic.domain.model.Subject
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toSubject(): Subject = Subject(id = this[SubjectTable.id], name = this[SubjectTable.name])

fun ResultRow.toLessonType(): LessonType = LessonType(id = this[LessonTypeTable.id], name = this[LessonTypeTable.name])

fun ResultRow.toLesson(): Lesson =
    Lesson(
        id = this[LessonTable.id],
        subjectId = this[LessonTable.subjectId],
        lessonTypeId = this[LessonTable.lessonTypeId],
        title = this[LessonTable.title],
        difficultyLevel = this[LessonTable.difficultyLevel],
    )

fun ResultRow.toExercise(): Exercise =
    Exercise(
        id = this[ExerciseTable.id],
        lessonId = this[ExerciseTable.lessonId],
        content = this[ExerciseTable.content],
        conceptTested = this[ExerciseTable.conceptTested],
    )
