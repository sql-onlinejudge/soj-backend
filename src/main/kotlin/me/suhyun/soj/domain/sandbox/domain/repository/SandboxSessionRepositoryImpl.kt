package me.suhyun.soj.domain.sandbox.domain.repository

import me.suhyun.soj.domain.sandbox.domain.entity.SandboxSessionEntity
import me.suhyun.soj.domain.sandbox.domain.entity.SandboxSessionTable
import me.suhyun.soj.domain.sandbox.domain.model.SandboxSession
import me.suhyun.soj.domain.sandbox.domain.model.SandboxStatus
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
            this.status = SandboxStatus.ACTIVE
            this.createdAt = session.createdAt
        }
        return SandboxSessionEntity.from(entity)
    }

    override fun findBySessionKey(sessionKey: String): SandboxSession? {
        return SandboxSessionEntity
            .find { SandboxSessionTable.sessionKey eq sessionKey }
            .firstOrNull()
            ?.let { SandboxSessionEntity.from(it) }
    }

    override fun findByUserId(userId: String): List<SandboxSession> {
        return SandboxSessionEntity
            .find { SandboxSessionTable.userId eq userId }
            .orderBy(SandboxSessionTable.createdAt to org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { SandboxSessionEntity.from(it) }
    }

    override fun findExpiredActive(): List<SandboxSession> {
        return SandboxSessionEntity
            .find {
                (SandboxSessionTable.status eq SandboxStatus.ACTIVE) and
                (SandboxSessionTable.expiresAt less LocalDateTime.now())
            }
            .map { SandboxSessionEntity.from(it) }
    }

    override fun updateStatus(id: Long, status: SandboxStatus) {
        val entity = SandboxSessionEntity.findById(id) ?: return
        entity.status = status
        entity.updatedAt = LocalDateTime.now()
    }

    override fun reactivate(id: Long, expiresAt: LocalDateTime) {
        val entity = SandboxSessionEntity.findById(id) ?: return
        entity.status = SandboxStatus.ACTIVE
        entity.expiresAt = expiresAt
        entity.updatedAt = LocalDateTime.now()
    }
}
