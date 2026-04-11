package me.suhyun.soj.domain.sandbox.domain.entity

import me.suhyun.soj.global.common.entity.BaseTable
import org.jetbrains.exposed.sql.javatime.datetime

@Suppress("MagicNumber")
object SandboxSessionTable : BaseTable("sandbox_sessions") {
    val sessionKey = varchar("session_key", 36).uniqueIndex()
    val userId = varchar("user_id", 36).nullable()
    val dbSchema = varchar("schema_name", 64)
    val extractedSql = text("extracted_sql")
    val expiresAt = datetime("expires_at")
}
