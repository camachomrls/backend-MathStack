package com.mathstack.users.infrastructure.persistence

import com.mathstack.academic.infrastructure.persistence.SubjectTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UserProficiencyTable : Table("user_proficiencies") {
    val id = integer("id").autoIncrement()
    val userId = uuid("user_id").references(UserTable.id)
    val subjectId = integer("subject_id").references(SubjectTable.id)
    val proficiencyLevel = integer("proficiency_level") // e.g., 1=Beginner, 2=Intermediate, 3=Advanced
    val lastEvaluatedAt = datetime("last_evaluated_at")

    override val primaryKey = PrimaryKey(id)
}
