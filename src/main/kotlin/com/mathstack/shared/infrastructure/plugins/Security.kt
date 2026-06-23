package com.mathstack.shared.infrastructure.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.config.ApplicationConfig

fun Application.configureSecurity() {
    val jwtConfig = JwtConfig.from(environment.config)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withIssuer(jwtConfig.issuer)
                    .withAudience(jwtConfig.audience)
                    .build(),
            )
            validate { credential ->
                val hasAudience = credential.payload.audience.contains(jwtConfig.audience)
                val userId = credential.payload.getClaim("user_id").asString()
                    ?: credential.payload.subject

                if (hasAudience && !userId.isNullOrBlank()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

private data class JwtConfig(
    val issuer: String,
    val audience: String,
    val realm: String,
    val secret: String,
) {
    companion object {
        fun from(config: ApplicationConfig): JwtConfig =
            JwtConfig(
                issuer = com.mathstack.shared.infrastructure.config.Env.get("JWT_ISSUER") ?: config.property("jwt.issuer").getString(),
                audience = com.mathstack.shared.infrastructure.config.Env.get("JWT_AUDIENCE") ?: config.property("jwt.audience").getString(),
                realm = com.mathstack.shared.infrastructure.config.Env.get("JWT_REALM") ?: config.property("jwt.realm").getString(),
                secret = com.mathstack.shared.infrastructure.config.Env.get("JWT_SECRET") ?: config.property("jwt.secret").getString(),
            )
    }
}
