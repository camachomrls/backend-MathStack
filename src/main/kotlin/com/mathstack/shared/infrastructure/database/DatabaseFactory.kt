package com.mathstack.shared.infrastructure.database

import com.mathstack.academic.infrastructure.persistence.ExerciseTable
import com.mathstack.academic.infrastructure.persistence.LessonTable
import com.mathstack.academic.infrastructure.persistence.LessonTypeTable
import com.mathstack.academic.infrastructure.persistence.SubjectTable
import com.mathstack.practice.infrastructure.persistence.DiagnosticResultsTable
import com.mathstack.practice.infrastructure.persistence.ExerciseAttemptsTable
import com.mathstack.practice.infrastructure.persistence.LearningPathsTable
import com.mathstack.practice.infrastructure.persistence.PracticeSessionsTable
import com.mathstack.store.infrastructure.persistence.ItemTypeTable
import com.mathstack.store.infrastructure.persistence.StoreItemTable
import com.mathstack.store.infrastructure.persistence.UserInventoryTable
import com.mathstack.users.infrastructure.persistence.UserGamificationStatsTable
import com.mathstack.users.infrastructure.persistence.UserTable
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        // Conexión a la base de datos usando HikariCP
        Database.connect(hikari(config))
        
        // ¡Magia! Esto crea todas las tablas automáticamente si no existen
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UserTable,
                UserGamificationStatsTable,
                SubjectTable,
                LessonTypeTable,
                LessonTable,
                ExerciseTable,
                ItemTypeTable,
                StoreItemTable,
                UserInventoryTable,
                DiagnosticResultsTable,
                LearningPathsTable,
                PracticeSessionsTable,
                ExerciseAttemptsTable
            )
        }
    }

    private fun hikari(config: ApplicationConfig): HikariDataSource {
        return HikariDataSource().apply {
            jdbcUrl = config.property("database.url").getString()
            username = config.property("database.user").getString()
            password = config.property("database.password").getString()
            driverClassName = config.property("database.driver").getString()
            
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 30000
            connectionTimeout = 2000
            maxLifetime = 1800000
        }
    }
}
