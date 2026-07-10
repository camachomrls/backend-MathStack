package com.mathstack.social.infrastructure.rest

import com.mathstack.social.application.*
import com.mathstack.social.infrastructure.rest.dto.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.groupsRouting() {
    val createGroup by inject<CreateGroupUseCase>()
    val addGroupMember by inject<AddGroupMemberUseCase>()
    val listGroups by inject<ListGroupsUseCase>()
    val getGroupDetails by inject<GetGroupDetailsUseCase>()
    val updateGroupActiveLevel by inject<UpdateGroupActiveLevelUseCase>()

    authenticate("auth-jwt") {
        route("/api/v1/social/groups") {
            
            get {
                val principal = call.principal<JWTPrincipal>() ?: throw com.mathstack.shared.domain.exception.UnauthorizedException("Token missing")
                val userIdStr = principal.payload.getClaim("user_id")?.asString() ?: principal.payload.subject
                val userId = UUID.fromString(userIdStr)
                
                val groups = listGroups(userId)
                call.respond(HttpStatusCode.OK, groups.map { it.toResponse() })
            }
            
            post {
                val principal = call.principal<JWTPrincipal>() ?: throw com.mathstack.shared.domain.exception.UnauthorizedException("Token missing")
                val userIdStr = principal.payload.getClaim("user_id")?.asString() ?: principal.payload.subject
                val userId = UUID.fromString(userIdStr)
                val request = call.receive<CreateGroupRequest>()
                
                val command = CreateGroupCommand(
                    creatorId = userId,
                    name = request.name,
                    description = request.description,
                    subject = request.subject,
                    maxMembers = request.maxMembers
                )
                
                val group = createGroup(command)
                call.respond(HttpStatusCode.Created, group.toResponse())
            }
            
            get("/{id}") {
                val groupIdStr = call.parameters["id"] ?: throw IllegalArgumentException("groupId is required")
                val groupId = UUID.fromString(groupIdStr)
                
                val groupDetails = getGroupDetails(groupId)
                call.respond(HttpStatusCode.OK, groupDetails.toResponse())
            }
            
            post("/{id}/members") {
                val groupIdStr = call.parameters["id"] ?: throw IllegalArgumentException("groupId is required")
                val groupId = UUID.fromString(groupIdStr)
                val request = call.receive<AddGroupMemberRequest>()
                
                val command = AddGroupMemberCommand(
                    groupId = groupId,
                    identifier = request.identifier
                )
                
                val member = addGroupMember(command)
                call.respond(HttpStatusCode.OK, mapOf("status" to "success", "message" to "Member added successfully"))
            }
            
            post("/{id}/active-level") {
                val principal = call.principal<JWTPrincipal>() ?: throw com.mathstack.shared.domain.exception.UnauthorizedException("Token missing")
                val userIdStr = principal.payload.getClaim("user_id")?.asString() ?: principal.payload.subject
                val userId = UUID.fromString(userIdStr)
                val groupIdStr = call.parameters["id"] ?: throw IllegalArgumentException("groupId is required")
                val groupId = UUID.fromString(groupIdStr)
                
                val request = call.receive<UpdateGroupActiveLevelRequest>()
                updateGroupActiveLevel(groupId, userId, UUID.fromString(request.levelId))
                
                call.respond(HttpStatusCode.OK, mapOf("status" to "success", "message" to "Active level updated"))
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class UpdateGroupActiveLevelRequest(
    val levelId: String
)
