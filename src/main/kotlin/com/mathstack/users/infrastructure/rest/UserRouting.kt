package com.mathstack.users.infrastructure.rest

import com.mathstack.shared.domain.exception.ValidationException
import com.mathstack.users.application.CreateUserUseCase
import com.mathstack.users.application.DeleteUserUseCase
import com.mathstack.users.application.GetUserProfileUseCase
import com.mathstack.users.application.UpdateGamificationStatsUseCase
import com.mathstack.users.application.UpdateUserUseCase
import com.mathstack.users.infrastructure.rest.dto.CreateUserRequest
import com.mathstack.users.infrastructure.rest.dto.UpdateGamificationStatsRequest
import com.mathstack.users.infrastructure.rest.dto.UpdateUserRequest
import com.mathstack.users.infrastructure.rest.dto.toCommand
import com.mathstack.users.infrastructure.rest.dto.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID
import org.koin.ktor.ext.inject

fun Route.userRouting() {
    val createUserUseCase by inject<CreateUserUseCase>()
    val deleteUserUseCase by inject<DeleteUserUseCase>()
    val getUserProfileUseCase by inject<GetUserProfileUseCase>()
    val updateGamificationStatsUseCase by inject<UpdateGamificationStatsUseCase>()
    val updateUserUseCase by inject<UpdateUserUseCase>()

    route("/api/v1/users") {
        post {
            val request = call.receive<CreateUserRequest>()
            val createdProfile = createUserUseCase(request.toCommand())

            call.respond(HttpStatusCode.Created, createdProfile.toResponse())
        }

        authenticate("auth-jwt") {
            get("/me") {
                val userId = call.jwtUserId()
                val profile = getUserProfileUseCase(userId)

                call.respond(HttpStatusCode.OK, profile.toResponse())
            }

            get("/{id}") {
                val userId = call.uuidPath("id")
                val profile = getUserProfileUseCase(userId)

                call.respond(HttpStatusCode.OK, profile.toResponse())
            }

            patch("/{id}") {
                val userId = call.uuidPath("id")
                val request = call.receive<UpdateUserRequest>()
                val updatedUser = updateUserUseCase(userId, request.toCommand())

                call.respond(HttpStatusCode.OK, updatedUser.toResponse())
            }

            patch("/{id}/gamification-stats") {
                val userId = call.uuidPath("id")
                val request = call.receive<UpdateGamificationStatsRequest>()
                val updatedStats = updateGamificationStatsUseCase(userId, request.toCommand())

                call.respond(HttpStatusCode.OK, updatedStats.toResponse())
            }

            delete("/{id}") {
                val userId = call.uuidPath("id")
                deleteUserUseCase(userId)

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun ApplicationCall.uuidPath(name: String): UUID {
    val rawValue = parameters[name]
        ?: throw ValidationException("$name path parameter is required")

    return runCatching { UUID.fromString(rawValue) }
        .getOrElse { throw ValidationException("$name must be a valid UUID") }
}

private fun ApplicationCall.jwtUserId(): UUID {
    val principal = principal<JWTPrincipal>()
        ?: throw ValidationException("JWT principal is required")
    val rawUserId = principal.payload.getClaim("user_id").asString()
        ?: principal.payload.subject
        ?: throw ValidationException("JWT must contain user_id or subject")

    return runCatching { UUID.fromString(rawUserId) }
        .getOrElse { throw ValidationException("JWT user identifier must be a valid UUID") }
}
