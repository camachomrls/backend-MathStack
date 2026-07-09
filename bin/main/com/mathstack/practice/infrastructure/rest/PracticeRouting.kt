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

            get("/diagnostics/generate") {
                val generateQuiz = org.koin.java.KoinJavaComponent.getKoin().get<com.mathstack.practice.application.GenerateDiagnosticQuizUseCase>()
                val exercises = generateQuiz()
                // Map exercises to DTO (assume toResponse exists in AcademicDtos)
                val responseList = exercises.map { com.mathstack.academic.infrastructure.rest.dto.ExerciseResponse(
                    id = it.id.toString(),
                    lessonId = it.lessonId.toString(),
                    content = it.content,
                    conceptTested = it.conceptTested
                ) }
                call.respond(HttpStatusCode.OK, responseList)
            }

            post("/diagnostics") {
                val userIdStr = call.parameters["userId"] ?: throw IllegalArgumentException("userId is required")
                val userId = UUID.fromString(userIdStr)
                val request = call.receive<com.mathstack.practice.infrastructure.rest.dto.SubmitDiagnosticRequest>()
                val submitDiagnostic = org.koin.java.KoinJavaComponent.getKoin().get<com.mathstack.practice.application.SubmitDiagnosticAnswersUseCase>()
                
                val answers = request.answers.map { 
                    com.mathstack.practice.application.DiagnosticAnswer(UUID.fromString(it.exerciseId), it.isCorrect) 
                }
                
                val command = com.mathstack.practice.application.SubmitDiagnosticAnswersCommand(
                    userId = userId,
                    answers = answers
                )
                
                val results = submitDiagnostic(command)
                
                // Map the subjectIds to their names using AcademicUseCase or direct repo query.
                // Since this is a routing block, we can resolve subject names manually or just return subjectId for the frontend to map.
                // Let's just return a generic map and the frontend can map it, or we fetch the subject names here.
                val listSubjects = org.koin.java.KoinJavaComponent.getKoin().get<com.mathstack.academic.application.ListSubjectsUseCase>()
                val subjects = listSubjects()
                
                val responseList = results.map { res ->
                    val subjectName = subjects.find { it.id == res.subjectId }?.name ?: "Materia desconocida"
                    mapOf(
                        "subjectId" to res.subjectId,
                        "subject" to subjectName,
                        "score" to res.scorePercentage
                    )
                }
                
                call.respond(HttpStatusCode.OK, responseList)
            }
        }
    }
}
