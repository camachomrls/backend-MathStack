package com.mathstack.social.application

import com.mathstack.social.domain.repository.GroupRepository
import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.shared.domain.exception.UnauthorizedException
import java.util.UUID

class UpdateGroupActiveLevelUseCase(
    private val groupRepository: GroupRepository
) {
    operator fun invoke(groupId: UUID, userId: UUID, levelId: UUID) {
        val group = groupRepository.getGroupById(groupId)
            ?: throw NotFoundException("Group not found")

        if (group.creatorId != userId) {
            throw UnauthorizedException("Only the group creator can change the active level")
        }

        groupRepository.updateGroup(group.copy(activeLevelId = levelId))
    }
}
