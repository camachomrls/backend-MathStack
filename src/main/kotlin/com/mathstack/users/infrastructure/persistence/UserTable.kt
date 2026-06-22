package com.mathstack.users.infrastructure.persistence

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UserTable : Table("users") {
    val id = uuid("id")
    val firebaseUid = varchar("firebase_uid", 255).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 50)
    val passwordHash = varchar("password_hash", 255)
    val accessLevel = varchar("access_level", 50)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}
