package com.mathstack.shared.domain.exception

open class ApiException(
    override val message: String,
    val code: String,
) : RuntimeException(message)

class NotFoundException(message: String) : ApiException(message, "not_found")

class ValidationException(message: String) : ApiException(message, "validation_error")

class ConflictException(message: String) : ApiException(message, "conflict")

class UnauthorizedException(message: String) : ApiException(message, "unauthorized")

class BusinessRuleException(message: String) : ApiException(message, "business_rule_violation")
