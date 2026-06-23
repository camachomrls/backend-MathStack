package com.mathstack.shared.infrastructure.plugins

import com.mathstack.shared.domain.exception.UnauthorizedException
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

class AuthorizedRouteSelector(private val roles: List<String>) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Constant
    }
}

fun Route.authorize(vararg roles: String, build: Route.() -> Unit): Route {
    val authorizedRoute = createChild(AuthorizedRouteSelector(roles.toList()))
    authorizedRoute.intercept(ApplicationCallPipeline.Plugins) {
        val principal = call.principal<JWTPrincipal>() ?: throw UnauthorizedException("Token missing or invalid")
        val userRole = principal.payload.getClaim("access_level")?.asString() ?: "USER"
        if (!roles.contains(userRole)) {
            throw com.mathstack.shared.domain.exception.ApiException("Forbidden: Requires roles ${roles.joinToString()}", "forbidden")
        }
    }
    authorizedRoute.build()
    return authorizedRoute
}
