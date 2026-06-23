package com.mathstack.shared.infrastructure.config

import io.github.cdimascio.dotenv.dotenv

object Env {
    private val dotenv = dotenv {
        ignoreIfMissing = true
    }

    fun get(key: String): String? = dotenv[key] ?: System.getenv(key)
}
