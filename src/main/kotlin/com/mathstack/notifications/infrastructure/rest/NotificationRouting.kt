package com.mathstack.notifications.infrastructure.rest

import com.mathstack.notifications.application.RegisterDeviceTokenCommand
import com.mathstack.notifications.application.RegisterDeviceTokenUseCase
import com.mathstack.notifications.application.UpdateNotificationPreferencesCommand
import com.mathstack.notifications.application.UpdateNotificationPreferencesUseCase
import com.mathstack.notifications.infrastructure.rest.dto.RegisterDeviceTokenRequest
import com.mathstack.notifications.infrastructure.rest.dto.UpdateNotificationPreferencesRequest
import com.mathstack.notifications.infrastructure.rest.dto.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.notificationRouting() {
    val updatePreferences by inject<UpdateNotificationPreferencesUseCase>()
    val registerToken by inject<RegisterDeviceTokenUseCase>()

    authenticate("auth-jwt") {
        route("/api/v1/notifications") {
            put("/preferences") {
                val principal = call.principal<JWTPrincipal>() ?: throw com.mathstack.shared.domain.exception.UnauthorizedException("Token missing")
                val userIdStr = principal.payload.getClaim("user_id")?.asString() ?: principal.payload.subject
                val userId = UUID.fromString(userIdStr)
                val request = call.receive<UpdateNotificationPreferencesRequest>()
                
                val command = UpdateNotificationPreferencesCommand(
                    userId = userId,
                    preferredTimeInUtc = request.preferredTimeInUtc,
                    isEnabled = request.isEnabled
                )
                val preferences = updatePreferences(command)
                call.respond(HttpStatusCode.OK, preferences.toResponse())
            }

            post("/tokens") {
                val principal = call.principal<JWTPrincipal>() ?: throw com.mathstack.shared.domain.exception.UnauthorizedException("Token missing")
                val userIdStr = principal.payload.getClaim("user_id")?.asString() ?: principal.payload.subject
                val userId = UUID.fromString(userIdStr)
                val request = call.receive<RegisterDeviceTokenRequest>()
                
                val command = RegisterDeviceTokenCommand(
                    userId = userId,
                    fcmToken = request.fcmToken
                )
                val token = registerToken(command)
                call.respond(HttpStatusCode.Created, token.toResponse())
            }
        }
    }
}
