package com.mathstack.users.infrastructure.persistence

import com.mathstack.users.domain.repository.UserProficiencyRepository
import java.time.LocalDateTime
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PostgresUserProficiencyRepository : UserProficiencyRepository {
    override fun saveProficiency(userId: UUID, subjectId: Int, level: Int) {
        transaction {
            val existing = UserProficiencyTable.selectAll().where {
                (UserProficiencyTable.userId eq userId) and (UserProficiencyTable.subjectId eq subjectId)
            }.singleOrNull()

            if (existing != null) {
                UserProficiencyTable.update({ UserProficiencyTable.id eq existing[UserProficiencyTable.id] }) {
                    it[proficiencyLevel] = level
                    it[lastEvaluatedAt] = LocalDateTime.now()
                }
            } else {
                UserProficiencyTable.insert {
                    it[this.userId] = userId
                    it[this.subjectId] = subjectId
                    it[proficiencyLevel] = level
                    it[lastEvaluatedAt] = LocalDateTime.now()
                }
            }
        }
    }

    override fun getProficiency(userId: UUID, subjectId: Int): Int? {
        return transaction {
            UserProficiencyTable.selectAll().where {
                (UserProficiencyTable.userId eq userId) and (UserProficiencyTable.subjectId eq subjectId)
            }.singleOrNull()?.get(UserProficiencyTable.proficiencyLevel)
        }
    }

    override fun getAllProficiencies(userId: UUID): Map<Int, Int> {
        return transaction {
            UserProficiencyTable.selectAll().where {
                UserProficiencyTable.userId eq userId
            }.associate { it[UserProficiencyTable.subjectId] to it[UserProficiencyTable.proficiencyLevel] }
        }
    }
}
