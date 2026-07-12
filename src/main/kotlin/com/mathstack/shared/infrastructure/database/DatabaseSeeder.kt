package com.mathstack.shared.infrastructure.database

import com.mathstack.academic.infrastructure.persistence.ExerciseTable
import com.mathstack.academic.infrastructure.persistence.LessonTable
import com.mathstack.academic.infrastructure.persistence.LessonTypeTable
import com.mathstack.academic.infrastructure.persistence.SubjectTable
import com.mathstack.admin.infrastructure.persistence.AdminChallengesTable
import com.mathstack.users.infrastructure.persistence.UserGamificationStatsTable
import com.mathstack.users.infrastructure.persistence.UserTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.UUID

object DatabaseSeeder {
    fun seed() {
        transaction {
            // Seed Admin User
            val adminEmail = "admin@mathstack.com"
            val existingAdmin = UserTable.selectAll().where { UserTable.email eq adminEmail }.singleOrNull()
            if (existingAdmin == null) {
                val adminId = UUID.randomUUID()
                UserTable.insert {
                    it[id] = adminId
                    it[firebaseUid] = "admin-firebase-uid"
                    it[email] = adminEmail
                    it[username] = "SuperAdmin"
                    it[passwordHash] = org.mindrot.jbcrypt.BCrypt.hashpw("admin123", org.mindrot.jbcrypt.BCrypt.gensalt(12))
                    it[accessLevel] = "ADMIN"
                    it[createdAt] = LocalDateTime.now()
                }
                UserGamificationStatsTable.insert {
                    it[userId] = adminId
                    it[coins] = 9999
                    it[currentLevel] = 100
                    it[xpPoints] = 99999
                    it[lessonsCompletedCount] = 0
                    it[currentStreak] = 0
                    it[maxStreak] = 0
                    it[minutesPracticed] = 0
                }
            }

            // Seed Academic Data
            // Limpieza de ejercicios "42" (dummy) del seeder anterior para forzar la actualización
            val dummyExercises = ExerciseTable.selectAll().where { ExerciseTable.conceptTested eq "42" }.count()
            if (dummyExercises > 0L) {
                org.jetbrains.exposed.sql.transactions.transaction {
                    ExerciseTable.deleteWhere { ExerciseTable.conceptTested eq "42" }
                    // Tambien podemos limpiar las lecciones generadas si es necesario, pero borrar ejercicios '42' permite que la tabla quede limpia para este caso.
                }
            }

            val existingExercises = ExerciseTable.selectAll().count()
            if (existingExercises == 0L) {
                val lessonTypeId = LessonTypeTable.selectAll().firstOrNull()?.get(LessonTypeTable.id) ?: return@transaction
                
                val subjects = SubjectTable.selectAll().toList()
                val aritmeticaId = subjects.find { it[SubjectTable.name] == "Aritmética" }?.get(SubjectTable.id) ?: return@transaction
                val algebraId = subjects.find { it[SubjectTable.name] == "Álgebra" }?.get(SubjectTable.id) ?: return@transaction

                // Lecciones de Aritmética
                val arLesson1Id = UUID.randomUUID()
                LessonTable.insert {
                    it[id] = arLesson1Id
                    it[subjectId] = aritmeticaId
                    it[this.lessonTypeId] = lessonTypeId
                    it[title] = "Suma y Resta Básica"
                    it[difficultyLevel] = 1
                    it[content] = "Aprenderás a sumar y restar números enteros."
                }
                val arLesson2Id = UUID.randomUUID()
                LessonTable.insert {
                    it[id] = arLesson2Id
                    it[subjectId] = aritmeticaId
                    it[this.lessonTypeId] = lessonTypeId
                    it[title] = "Multiplicación y División"
                    it[difficultyLevel] = 2
                    it[content] = "Aprenderás operaciones multiplicativas."
                }

                // Lecciones de Álgebra
                val alLesson1Id = UUID.randomUUID()
                LessonTable.insert {
                    it[id] = alLesson1Id
                    it[subjectId] = algebraId
                    it[this.lessonTypeId] = lessonTypeId
                    it[title] = "Introducción a Ecuaciones Lineales"
                    it[difficultyLevel] = 1
                    it[content] = "Conceptos básicos de ecuaciones con una variable."
                }
                val alLesson2Id = UUID.randomUUID()
                LessonTable.insert {
                    it[id] = alLesson2Id
                    it[subjectId] = algebraId
                    it[this.lessonTypeId] = lessonTypeId
                    it[title] = "Sistemas de Ecuaciones"
                    it[difficultyLevel] = 2
                    it[content] = "Resolveremos sistemas 2x2."
                }

                // Ejercicios Aritmética
                insertExercise(arLesson1Id, "15 + 27", "42", listOf("32", "42", "52", "40"), "Suma las unidades primero.", "Suma Básica")
                insertExercise(arLesson1Id, "50 - 18", "32", listOf("32", "22", "42", "28"), "Resta 10 y luego 8.", "Resta Básica")
                insertExercise(arLesson1Id, "100 - 45 + 5", "60", listOf("50", "60", "70", "55"), "Hazlo de izquierda a derecha.", "Operaciones Combinadas")
                insertExercise(arLesson2Id, "12 * 8", "96", listOf("86", "96", "106", "90"), "Multiplica 10*8 y luego suma 2*8.", "Multiplicación")
                insertExercise(arLesson2Id, "144 / 12", "12", listOf("10", "12", "14", "11"), "Busca qué número multiplicado por 12 da 144.", "División")

                // Ejercicios Álgebra L1 (con stepByStep complejo)
                val algebraEx1 = """
                    {
                      "question": "Resuelve para x: 2x + 5 = 13",
                      "correctAnswer": "4",
                      "options": ["3", "4", "5", "6"],
                      "hint": "Resta 5 de ambos lados primero.",
                      "stepByStep": {
                        "rules": ["Restar de ambos lados", "Dividir ambos lados"],
                        "steps": [
                          { "description": "Resta 5 de ambos lados", "expression": "2x = 8" },
                          { "description": "Divide entre 2", "expression": "x = 4" }
                        ]
                      }
                    }
                """.trimIndent()
                ExerciseTable.insert {
                    it[id] = UUID.randomUUID()
                    it[lessonId] = alLesson1Id
                    it[content] = algebraEx1
                    it[conceptTested] = "Ecuación Lineal Simple"
                }

                val algebraEx2 = """
                    {
                      "question": "Resuelve para y: 3y - 7 = 14",
                      "correctAnswer": "7",
                      "options": ["6", "7", "8", "9"],
                      "hint": "Suma 7 a ambos lados.",
                      "stepByStep": {
                        "rules": ["Sumar a ambos lados", "Dividir ambos lados"],
                        "steps": [
                          { "description": "Suma 7 a ambos lados", "expression": "3y = 21" },
                          { "description": "Divide entre 3", "expression": "y = 7" }
                        ]
                      }
                    }
                """.trimIndent()
                ExerciseTable.insert {
                    it[id] = UUID.randomUUID()
                    it[lessonId] = alLesson1Id
                    it[content] = algebraEx2
                    it[conceptTested] = "Ecuación Lineal Simple"
                }

                val algebraEx3 = """
                    {
                      "question": "Resuelve: 5x = 3x + 10",
                      "correctAnswer": "5",
                      "options": ["2", "4", "5", "10"],
                      "hint": "Agrupa las x de un solo lado.",
                      "stepByStep": {
                        "rules": ["Restar variables de un lado", "Dividir"],
                        "steps": [
                          { "description": "Resta 3x de ambos lados", "expression": "2x = 10" },
                          { "description": "Divide entre 2", "expression": "x = 5" }
                        ]
                      }
                    }
                """.trimIndent()
                ExerciseTable.insert {
                    it[id] = UUID.randomUUID()
                    it[lessonId] = alLesson1Id
                    it[content] = algebraEx3
                    it[conceptTested] = "Agrupación de Variables"
                }

                val algebraEx4 = """
                    {
                      "question": "Si x + y = 10 y x - y = 2, ¿cuánto vale x?",
                      "correctAnswer": "6",
                      "options": ["4", "5", "6", "8"],
                      "hint": "Suma ambas ecuaciones.",
                      "stepByStep": {
                        "rules": ["Método de suma/resta"],
                        "steps": [
                          { "description": "Suma las ecuaciones (x+x y y-y)", "expression": "2x = 12" },
                          { "description": "Divide entre 2", "expression": "x = 6" }
                        ]
                      }
                    }
                """.trimIndent()
                ExerciseTable.insert {
                    it[id] = UUID.randomUUID()
                    it[lessonId] = alLesson2Id
                    it[content] = algebraEx4
                    it[conceptTested] = "Sistema 2x2"
                }
                
                val algebraEx5 = """
                    {
                      "question": "Si y = 2x y x + y = 9, ¿cuánto vale y?",
                      "correctAnswer": "6",
                      "options": ["3", "6", "9", "2"],
                      "hint": "Sustituye y por 2x en la segunda ecuación.",
                      "stepByStep": {
                        "rules": ["Método de sustitución"],
                        "steps": [
                          { "description": "Sustituye y en x + y = 9", "expression": "x + 2x = 9" },
                          { "description": "Simplifica", "expression": "3x = 9" },
                          { "description": "Resuelve x", "expression": "x = 3" },
                          { "description": "Encuentra y = 2(3)", "expression": "y = 6" }
                        ]
                      }
                    }
                """.trimIndent()
                ExerciseTable.insert {
                    it[id] = UUID.randomUUID()
                    it[lessonId] = alLesson2Id
                    it[content] = algebraEx5
                    it[conceptTested] = "Sustitución"
                }
            }

            // Seed Challenge (Maratón de Álgebra)
            val existingChallenge = AdminChallengesTable.selectAll().where { AdminChallengesTable.title eq "Maratón de Álgebra" }.count()
            if (existingChallenge == 0L) {
                val algebraId = SubjectTable.selectAll().firstOrNull { it[SubjectTable.name] == "Álgebra" }?.get(SubjectTable.id)
                AdminChallengesTable.insert {
                    it[id] = UUID.randomUUID()
                    it[title] = "Maratón de Álgebra"
                    it[description] = "Demuestra tu dominio resolviendo 5 ecuaciones seguidas sin fallar."
                    it[subjectId] = algebraId
                    it[difficulty] = "intermediate"
                    it[rewardCoins] = 50
                    it[rewardXp] = 100
                    it[targetScore] = 5
                    it[status] = "active"
                    it[createdAt] = LocalDateTime.now()
                    it[startDate] = LocalDateTime.now()
                    it[endDate] = LocalDateTime.now().plusDays(7)
                }
            }
        }
    }

    private fun insertExercise(lessonId: UUID, q: String, a: String, options: List<String>, hint: String, concept: String) {
        val optionsStr = options.joinToString(", ") { "\"$it\"" }
        val contentJson = """
            {
              "question": "$q",
              "correctAnswer": "$a",
              "options": [$optionsStr],
              "hint": "$hint",
              "stepByStep": {
                "rules": ["Operaciones básicas"],
                "steps": [
                  { "description": "Resuelve la operación", "expression": "$q = $a" }
                ]
              }
            }
        """.trimIndent()
        
        ExerciseTable.insert {
            it[id] = UUID.randomUUID()
            it[this.lessonId] = lessonId
            it[content] = contentJson
            it[conceptTested] = concept
        }
    }
}
