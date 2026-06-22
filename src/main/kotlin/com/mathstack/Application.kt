package com.mathstack

import com.mathstack.academic.infrastructure.rest.academicRouting
import com.mathstack.auth.infrastructure.rest.authRouting
import com.mathstack.practice.infrastructure.rest.practiceRouting
import com.mathstack.shared.infrastructure.database.DatabaseFactory
import com.mathstack.shared.infrastructure.plugins.configureDependencyInjection
import com.mathstack.shared.infrastructure.plugins.configureSecurity
import com.mathstack.shared.infrastructure.plugins.configureSerialization
import com.mathstack.shared.infrastructure.plugins.configureStatusPages
import com.mathstack.store.infrastructure.rest.storeRouting
import com.mathstack.users.infrastructure.rest.userRouting
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.module() {
    configureSerialization()
    configureStatusPages()
    configureDependencyInjection()
    configureSecurity()
    DatabaseFactory.init(environment.config)

    routing {
        userRouting()
        authRouting()
        academicRouting()
        storeRouting()
        practiceRouting()
    }
}
