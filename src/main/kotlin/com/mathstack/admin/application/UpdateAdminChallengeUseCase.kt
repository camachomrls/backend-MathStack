package com.mathstack.admin.application

import com.mathstack.admin.domain.model.AdminChallenge
import com.mathstack.admin.domain.repository.AdminChallengeRepository
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable

data class UpdateAdminChallengeCommand(
    val title: String?,
    val description: String?,
    val subjectId: Int?,
    val difficulty: String?,
    val startDate: String?,
    val endDate: String?,
    val rewardCoins: Int?,
    val rewardXp: Int?,
    val targetScore: Int?,
    val isActive: Boolean?
)

class UpdateAdminChallengeUseCase(
    private val repository: AdminChallengeRepository
) {
    operator fun invoke(id: UUID, command: UpdateAdminChallengeCommand): AdminChallenge? {
        val existingChallenge = repository.findById(id) ?: return null

        val updatedChallenge = existingChallenge.copy(
            title = command.title ?: existingChallenge.title,
            description = command.description ?: existingChallenge.description,
            subjectId = command.subjectId ?: existingChallenge.subjectId,
            difficulty = command.difficulty ?: existingChallenge.difficulty,
            startDate = command.startDate?.let { LocalDateTime.parse(it) } ?: existingChallenge.startDate,
            endDate = command.endDate?.let { LocalDateTime.parse(it) } ?: existingChallenge.endDate,
            rewardCoins = command.rewardCoins ?: existingChallenge.rewardCoins,
            rewardXp = command.rewardXp ?: existingChallenge.rewardXp,
            targetScore = command.targetScore ?: existingChallenge.targetScore,
            status = command.isActive?.let { if (it) "ACTIVE" else "INACTIVE" } ?: existingChallenge.status
        )

        return repository.update(updatedChallenge)
    }
}
