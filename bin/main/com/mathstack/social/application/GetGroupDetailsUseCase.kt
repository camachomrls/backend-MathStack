package com.mathstack.social.application

import com.mathstack.social.domain.model.Group
import com.mathstack.social.domain.repository.GroupRepository
import com.mathstack.users.domain.repository.UserRepository
import com.mathstack.shared.domain.exception.NotFoundException
import java.util.UUID

data class GroupMemberDto(
    val userId: UUID,
    val username: String,
    val role: String,
    val level: Int,
    val streak: Int,
    val xp: Int
)

data class GroupDetailsDto(
    val id: UUID,
    val name: String,
    val description: String?,
    val subject: String,
    val maxMembers: Int,
    val activeChallenges: Int,
    val totalXp: Int,
    val color: String,
    val activeLevelId: String?,
    val members: List<GroupMemberDto>
)

class GetGroupDetailsUseCase(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(groupId: UUID): GroupDetailsDto {
        val group = groupRepository.getGroupById(groupId) 
            ?: throw NotFoundException("Group not found")
            
        val groupMembers = groupRepository.getGroupMembers(groupId)
        
        val membersDto = groupMembers.map { member ->
            val user = userRepository.findUserById(member.userId)
            val stats = userRepository.findStatsByUserId(member.userId)
            
            GroupMemberDto(
                userId = member.userId,
                username = user?.username ?: "Unknown",
                role = member.role,
                level = stats?.currentLevel ?: 1,
                streak = stats?.currentStreak ?: 0,
                xp = stats?.xpPoints ?: 0
            )
        }.sortedByDescending { it.xp } // Sort by XP
        
        return GroupDetailsDto(
            id = group.id,
            name = group.name,
            description = group.description,
            subject = group.subject,
            maxMembers = group.maxMembers,
            activeChallenges = group.activeChallenges,
            totalXp = group.totalXp,
            color = group.color,
            activeLevelId = group.activeLevelId?.toString(),
            members = membersDto
        )
    }
}
