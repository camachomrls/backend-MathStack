package com.mathstack.users.domain.repository

import java.util.UUID

interface UserProficiencyRepository {
    fun saveProficiency(userId: UUID, subjectId: Int, level: Int)
    fun getProficiency(userId: UUID, subjectId: Int): Int?
    fun getAllProficiencies(userId: UUID): Map<Int, Int>
}
