package me.suhyun.soj.domain.sandbox.presentation.response

import java.time.LocalDateTime

data class SandboxSetupResponse(
    val sessionKey: String,
    val schemaName: String,
    val extractedSql: String,
    val expiresAt: LocalDateTime
)
