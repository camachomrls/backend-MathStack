package com.mathstack.auth.infrastructure.rest

import com.mathstack.auth.application.LoginUseCase
import com.mathstack.auth.application.RegisterUseCase
import com.mathstack.auth.infrastructure.rest.dto.LoginRequest
import com.mathstack.auth.infrastructure.rest.dto.RegisterRequest
import com.mathstack.auth.infrastructure.rest.dto.toCommand
import com.mathstack.auth.infrastructure.rest.dto.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.authRouting() {
    val loginUseCase by inject<LoginUseCase>()
    val registerUseCase by inject<RegisterUseCase>()
    val loginWithGoogleUseCase by inject<com.mathstack.auth.application.LoginWithGoogleUseCase>()

    route("/api/v1/auth") {
        post("/login") {
            val session = loginUseCase(call.receive<LoginRequest>().toCommand())
            call.respond(HttpStatusCode.OK, session.toResponse())
        }

        post("/google") {
            val session = loginWithGoogleUseCase(call.receive<com.mathstack.auth.infrastructure.rest.dto.LoginWithGoogleRequest>().toCommand())
            call.respond(HttpStatusCode.OK, session.toResponse())
        }

        post("/register") {
            val session = registerUseCase(call.receive<RegisterRequest>().toCommand())
            call.respond(HttpStatusCode.Created, session.toResponse())
        }
    }
}
