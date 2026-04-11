package me.suhyun.soj.domain.sandbox.domain.model

import java.time.LocalDateTime

data class SandboxSession(
    val id: Long? = null,
    val sessionKey: String,
    val userId: String?,
    val schemaName: String,
    val extractedSql: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
) {
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
    fun isOwnedBy(userId: String): Boolean = this.userId == userId
}
