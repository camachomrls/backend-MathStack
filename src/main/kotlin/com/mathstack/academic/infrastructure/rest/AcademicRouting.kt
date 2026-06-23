package com.mathstack.academic.infrastructure.rest

import com.mathstack.academic.application.CreateExerciseUseCase
import com.mathstack.academic.application.CreateLessonTypeUseCase
import com.mathstack.academic.application.CreateLessonUseCase
import com.mathstack.academic.application.CreateSubjectUseCase
import com.mathstack.academic.application.DeleteExerciseUseCase
import com.mathstack.academic.application.DeleteLessonUseCase
import com.mathstack.academic.application.GetExercisesByLessonUseCase
import com.mathstack.academic.application.GetLessonsBySubjectUseCase
import com.mathstack.academic.application.ListLessonTypesUseCase
import com.mathstack.academic.application.ListSubjectsUseCase
import com.mathstack.academic.infrastructure.rest.dto.CreateExerciseRequest
import com.mathstack.academic.infrastructure.rest.dto.CreateLessonRequest
import com.mathstack.academic.infrastructure.rest.dto.CreateLessonTypeRequest
import com.mathstack.academic.infrastructure.rest.dto.CreateSubjectRequest
import com.mathstack.academic.infrastructure.rest.dto.toCommand
import com.mathstack.academic.infrastructure.rest.dto.toResponse
import com.mathstack.academic.infrastructure.rest.dto.toUuid
import com.mathstack.academic.infrastructure.rest.dto.validName
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import com.mathstack.shared.infrastructure.plugins.authorize

fun Route.academicRouting() {
    val createSubject by inject<CreateSubjectUseCase>()
    val listSubjects by inject<ListSubjectsUseCase>()
    val createLessonType by inject<CreateLessonTypeUseCase>()
    val listLessonTypes by inject<ListLessonTypesUseCase>()
    val createLesson by inject<CreateLessonUseCase>()
    val lessonsBySubject by inject<GetLessonsBySubjectUseCase>()
    val createExercise by inject<CreateExerciseUseCase>()
    val exercisesByLesson by inject<GetExercisesByLessonUseCase>()
    val deleteLesson by inject<DeleteLessonUseCase>()
    val deleteExercise by inject<DeleteExerciseUseCase>()

    authenticate("auth-jwt") {
        route("/api/v1/academic") {
            get("/subjects") { call.respond(listSubjects().map { it.toResponse() }) }
            get("/lesson-types") { call.respond(listLessonTypes().map { it.toResponse() }) }
            get("/subjects/{subjectId}/lessons") {
                val subjectId = call.parameters["subjectId"]?.toIntOrNull()
                    ?: throw com.mathstack.shared.domain.exception.ValidationException("subjectId must be a valid integer")
                call.respond(lessonsBySubject(subjectId).map { it.toResponse() })
            }
            get("/lessons/{lessonId}/exercises") {
                val lessonId = (call.parameters["lessonId"] ?: "").toUuid("lessonId")
                call.respond(exercisesByLesson(lessonId).map { it.toResponse() })
            }

            authorize("ADMIN", "TEACHER") {
                post("/subjects") {
                    val subject = createSubject(call.receive<CreateSubjectRequest>().validName())
                    call.respond(HttpStatusCode.Created, subject.toResponse())
                }

                post("/lesson-types") {
                    val type = createLessonType(call.receive<CreateLessonTypeRequest>().validName())
                    call.respond(HttpStatusCode.Created, type.toResponse())
                }

                post("/lessons") {
                    val lesson = createLesson(call.receive<CreateLessonRequest>().toCommand())
                    call.respond(HttpStatusCode.Created, lesson.toResponse())
                }
                
                delete("/lessons/{id}") {
                    deleteLesson((call.parameters["id"] ?: "").toUuid("id"))
                    call.respond(HttpStatusCode.NoContent)
                }

                post("/exercises") {
                    val exercise = createExercise(call.receive<CreateExerciseRequest>().toCommand())
                    call.respond(HttpStatusCode.Created, exercise.toResponse())
                }
                
                delete("/exercises/{id}") {
                    deleteExercise((call.parameters["id"] ?: "").toUuid("id"))
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
