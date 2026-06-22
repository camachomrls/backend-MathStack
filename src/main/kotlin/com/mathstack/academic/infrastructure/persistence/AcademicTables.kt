package com.mathstack.academic.infrastructure.persistence

import org.jetbrains.exposed.sql.Table

object SubjectTable : Table("subjects") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

object LessonTypeTable : Table("lesson_types") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

object LessonTable : Table("lessons") {
    val id = uuid("id")
    val subjectId = integer("subject_id").references(SubjectTable.id)
    val lessonTypeId = integer("lesson_type_id").references(LessonTypeTable.id)
    val title = varchar("title", 200)
    val difficultyLevel = integer("difficulty_level")
    override val primaryKey = PrimaryKey(id)
}

object ExerciseTable : Table("exercises") {
    val id = uuid("id")
    val lessonId = uuid("lesson_id").references(LessonTable.id)
    val content = text("content")
    val conceptTested = varchar("concept_tested", 100).nullable()
    override val primaryKey = PrimaryKey(id)
}
