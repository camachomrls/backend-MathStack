package com.mathstack.admin.infrastructure.persistence

import org.jetbrains.exposed.sql.Table

object AdminSettingsTable : Table("admin_settings") {
    val key = varchar("key", 100)
    val value = text("value")

    override val primaryKey = PrimaryKey(key)
}
