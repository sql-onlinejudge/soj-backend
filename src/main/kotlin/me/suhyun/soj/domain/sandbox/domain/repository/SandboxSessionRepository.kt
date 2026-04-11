package me.suhyun.soj.domain.sandbox.domain.repository

import me.suhyun.soj.domain.sandbox.domain.model.SandboxSession
import java.time.LocalDateTime

interface SandboxSessionRepository {
    fun save(session: SandboxSession): SandboxSession
    fun findBySessionKey(sessionKey: String): SandboxSession?
    fun findExpiredBefore(dateTime: LocalDateTime): List<SandboxSession>
    fun softDelete(id: Long)
}
