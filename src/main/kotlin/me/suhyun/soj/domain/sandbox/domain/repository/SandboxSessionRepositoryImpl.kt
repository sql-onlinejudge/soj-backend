package me.suhyun.soj.domain.sandbox.domain.repository

import me.suhyun.soj.domain.sandbox.domain.entity.SandboxSessionEntity
import me.suhyun.soj.domain.sandbox.domain.entity.SandboxSessionTable
import me.suhyun.soj.domain.sandbox.domain.model.SandboxSession
import org.jetbrains.exposed.sql.and
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SandboxSessionRepositoryImpl : SandboxSessionRepository {

    override fun save(session: SandboxSession): SandboxSession {
        val entity = SandboxSessionEntity.new {
            this.sessionKey = session.sessionKey
            this.userId = session.userId
            this.schemaName = session.schemaName
            this.extractedSql = session.extractedSql
            this.expiresAt = session.expiresAt
            this.createdAt = session.createdAt
        }
        return SandboxSessionEntity.from(entity)
    }

    override fun findBySessionKey(sessionKey: String): SandboxSession? {
        return SandboxSessionEntity
            .find { (SandboxSessionTable.sessionKey eq sessionKey) and (SandboxSessionTable.deletedAt.isNull()) }
            .firstOrNull()
            ?.let { SandboxSessionEntity.from(it) }
    }

    override fun findExpiredBefore(dateTime: LocalDateTime): List<SandboxSession> {
        return SandboxSessionEntity
            .find { (SandboxSessionTable.expiresAt less dateTime) and (SandboxSessionTable.deletedAt.isNull()) }
            .map { SandboxSessionEntity.from(it) }
    }

    override fun softDelete(id: Long) {
        val entity = SandboxSessionEntity.findById(id) ?: return
        entity.deletedAt = LocalDateTime.now()
        entity.updatedAt = LocalDateTime.now()
    }
}
