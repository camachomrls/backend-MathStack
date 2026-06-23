package com.mathstack.shared.infrastructure.plugins

import com.mathstack.academic.academicModule
import com.mathstack.auth.authModule
import com.mathstack.practice.practiceModule
import com.mathstack.store.storeModule
import com.mathstack.users.userModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.dsl.module

import com.mathstack.admin.adminModule
import com.mathstack.notifications.notificationModule
import com.mathstack.social.socialModule

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        val configModule = module {
            single<ApplicationConfig> { environment.config }
        }

        modules(
            configModule,
            userModule,
            authModule,
            academicModule,
            storeModule,
            practiceModule,
            socialModule,
            notificationModule,
            adminModule
        )
    }
}
