package com.mathstack.social.domain.repository

import com.mathstack.social.domain.model.Group
import com.mathstack.social.domain.model.GroupMember
import java.util.UUID

interface GroupRepository {
    fun createGroup(group: Group): Group
    fun getGroupById(groupId: UUID): Group?
    fun updateGroup(group: Group): Group
    fun getGroupsByUserId(userId: UUID): List<Group>
    fun addMember(member: GroupMember): GroupMember
    fun getGroupMembers(groupId: UUID): List<GroupMember>
    fun getMemberCount(groupId: UUID): Int
    fun isMember(groupId: UUID, userId: UUID): Boolean
}
