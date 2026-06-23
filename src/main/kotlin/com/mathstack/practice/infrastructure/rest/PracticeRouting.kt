package com.mathstack.practice.infrastructure.rest

import com.mathstack.practice.application.GetStudentDashboardMetricsUseCase
import com.mathstack.practice.application.LogPracticeSessionCommand
import com.mathstack.practice.application.LogPracticeSessionUseCase
import com.mathstack.practice.application.RegisterExerciseAttemptCommand
import com.mathstack.practice.application.RegisterExerciseAttemptUseCase
import com.mathstack.practice.infrastructure.rest.dto.LogPracticeSessionRequest
import com.mathstack.practice.infrastructure.rest.dto.RegisterExerciseAttemptRequest
import com.mathstack.practice.infrastructure.rest.dto.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import java.time.LocalDate
import java.util.UUID

fun Route.practiceRouting() {
    val registerExerciseAttempt by inject<RegisterExerciseAttemptUseCase>()
    val logPracticeSession by inject<LogPracticeSessionUseCase>()
    val getDashboardMetrics by inject<GetStudentDashboardMetricsUseCase>()

    authenticate("auth-jwt") {
        route("/api/v1/practice/users/{userId}") {
            post("/attempts") {
                val userIdStr = call.parameters["userId"] ?: throw IllegalArgumentException("userId is required")
                val userId = UUID.fromString(userIdStr)
                val request = call.receive<RegisterExerciseAttemptRequest>()
                val command = RegisterExerciseAttemptCommand(
                    userId = userId,
                    exerciseId = UUID.fromString(request.exerciseId),
                    isCorrect = request.isCorrect
                )
                val attempt = registerExerciseAttempt(command)
                call.respond(HttpStatusCode.Created, attempt.toResponse())
            }

            post("/sessions") {
                val userIdStr = call.parameters["userId"] ?: throw IllegalArgumentException("userId is required")
                val userId = UUID.fromString(userIdStr)
                val request = call.receive<LogPracticeSessionRequest>()
                val command = LogPracticeSessionCommand(
                    userId = userId,
                    sessionDate = LocalDate.parse(request.sessionDate),
                    minutesSpent = request.minutesSpent
                )
                val session = logPracticeSession(command)
                call.respond(HttpStatusCode.OK, session.toResponse())
            }

            get("/dashboard") {
                val userIdStr = call.parameters["userId"] ?: throw IllegalArgumentException("userId is required")
                val userId = UUID.fromString(userIdStr)
                val metrics = getDashboardMetrics(userId)
                call.respond(HttpStatusCode.OK, metrics.toResponse())
            }

            post("/diagnostics") {
                val userIdStr = call.parameters["userId"] ?: throw IllegalArgumentException("userId is required")
                val userId = UUID.fromString(userIdStr)
                val request = call.receive<com.mathstack.practice.infrastructure.rest.dto.SubmitDiagnosticRequest>()
                val submitDiagnostic = org.koin.java.KoinJavaComponent.getKoin().get<com.mathstack.practice.application.SubmitDiagnosticAnswersUseCase>()
                val command = com.mathstack.practice.application.SubmitDiagnosticAnswersCommand(
                    userId = userId,
                    subjectId = request.subjectId,
                    score = request.score
                )
                val result = submitDiagnostic(command)
                call.respond(HttpStatusCode.Created, result.toResponse())
            }
        }
    }
}
