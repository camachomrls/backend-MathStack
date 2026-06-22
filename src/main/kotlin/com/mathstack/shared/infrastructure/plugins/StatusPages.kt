package com.mathstack.shared.infrastructure.plugins

import com.mathstack.shared.domain.exception.ApiException
import com.mathstack.shared.domain.exception.BusinessRuleException
import com.mathstack.shared.domain.exception.ConflictException
import com.mathstack.shared.domain.exception.NotFoundException
import com.mathstack.shared.domain.exception.UnauthorizedException
import com.mathstack.shared.domain.exception.ValidationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, cause.toErrorResponse())
        }
        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.toErrorResponse())
        }
        exception<ConflictException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, cause.toErrorResponse())
        }
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, cause.toErrorResponse())
        }
        exception<BusinessRuleException> { call, cause ->
            call.respond(HttpStatusCode.UnprocessableEntity, cause.toErrorResponse())
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(code = "internal_error", message = "Unexpected server error"),
            )
        }
    }
}

private fun ApiException.toErrorResponse(): ErrorResponse =
    ErrorResponse(code = code, message = message)

@Serializable
private data class ErrorResponse(
    val code: String,
    val message: String,
)
