package me.suhyun.soj.domain.sandbox.application.scheduler

import me.suhyun.soj.domain.sandbox.domain.repository.SandboxSessionRepository
import me.suhyun.soj.domain.sandbox.infrastructure.SandboxSchemaManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class SandboxCleanupScheduler(
    private val sandboxSessionRepository: SandboxSessionRepository,
    private val sandboxSchemaManager: SandboxSchemaManager
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = 600_000)
    @Transactional
    fun cleanupExpiredSessions() {
        val expired = sandboxSessionRepository.findExpiredBefore(LocalDateTime.now())
        expired.forEach { session ->
            sandboxSchemaManager.dropSchema(session.schemaName)
            sandboxSessionRepository.softDelete(session.id!!)
            log.info("Cleaned up expired sandbox session: {}", session.sessionKey)
        }
    }
}
