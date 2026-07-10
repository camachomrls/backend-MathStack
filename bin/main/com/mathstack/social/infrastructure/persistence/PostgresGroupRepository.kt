package com.mathstack.social.infrastructure.persistence

import com.mathstack.social.domain.model.Group
import com.mathstack.social.domain.model.GroupMember
import com.mathstack.social.domain.repository.GroupRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class PostgresGroupRepository : GroupRepository {
    
    private fun ResultRow.toGroup() = Group(
        id = this[GroupsTable.id],
        name = this[GroupsTable.name],
        description = this[GroupsTable.description],
        subject = this[GroupsTable.subject],
        maxMembers = this[GroupsTable.maxMembers],
        creatorId = this[GroupsTable.creatorId],
        createdAt = this[GroupsTable.createdAt],
        activeChallenges = this[GroupsTable.activeChallenges],
        totalXp = this[GroupsTable.totalXp],
        color = this[GroupsTable.color],
        activeLevelId = this[GroupsTable.activeLevelId]
    )

    private fun ResultRow.toGroupMember() = GroupMember(
        groupId = this[GroupMembersTable.groupId],
        userId = this[GroupMembersTable.userId],
        role = this[GroupMembersTable.role],
        joinedAt = this[GroupMembersTable.joinedAt]
    )

    override fun createGroup(group: Group): Group = transaction {
        GroupsTable.insert {
            it[id] = group.id
            it[name] = group.name
            it[description] = group.description
            it[subject] = group.subject
            it[maxMembers] = group.maxMembers
            it[creatorId] = group.creatorId
            it[createdAt] = group.createdAt
            it[activeChallenges] = group.activeChallenges
            it[totalXp] = group.totalXp
            it[color] = group.color
            it[activeLevelId] = group.activeLevelId
        }
        group
    }

    override fun getGroupById(groupId: UUID): Group? = transaction {
        GroupsTable.selectAll().where { GroupsTable.id eq groupId }
            .map { it.toGroup() }
            .singleOrNull()
    }

    override fun updateGroup(group: Group): Group = transaction {
        GroupsTable.update({ GroupsTable.id eq group.id }) {
            it[name] = group.name
            it[description] = group.description
            it[subject] = group.subject
            it[maxMembers] = group.maxMembers
            it[activeChallenges] = group.activeChallenges
            it[totalXp] = group.totalXp
            it[color] = group.color
            it[activeLevelId] = group.activeLevelId
        }
        group
    }

    override fun getGroupsByUserId(userId: UUID): List<Group> = transaction {
        GroupsTable.join(GroupMembersTable, JoinType.INNER, additionalConstraint = { GroupsTable.id eq GroupMembersTable.groupId })
            .selectAll().where { GroupMembersTable.userId eq userId }
            .map { it.toGroup() }
    }

    override fun addMember(member: GroupMember): GroupMember = transaction {
        GroupMembersTable.insert {
            it[groupId] = member.groupId
            it[userId] = member.userId
            it[role] = member.role
            it[joinedAt] = member.joinedAt
        }
        member
    }

    override fun getGroupMembers(groupId: UUID): List<GroupMember> = transaction {
        GroupMembersTable.selectAll().where { GroupMembersTable.groupId eq groupId }
            .map { it.toGroupMember() }
    }

    override fun getMemberCount(groupId: UUID): Int = transaction {
        GroupMembersTable.selectAll().where { GroupMembersTable.groupId eq groupId }.count().toInt()
    }

    override fun isMember(groupId: UUID, userId: UUID): Boolean = transaction {
        GroupMembersTable.selectAll().where { 
            (GroupMembersTable.groupId eq groupId) and (GroupMembersTable.userId eq userId) 
        }.count() > 0
    }
}
