package me.suhyun.soj.domain.sandbox.application.service

import me.suhyun.soj.domain.sandbox.domain.repository.SandboxSessionRepository
import me.suhyun.soj.domain.sandbox.exception.SandboxErrorCode
import me.suhyun.soj.domain.sandbox.infrastructure.SandboxSchemaManager
import me.suhyun.soj.domain.sandbox.presentation.response.SandboxSetupResponse
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Service
class SandboxReactivateService(
    private val sandboxSessionRepository: SandboxSessionRepository,
    private val sandboxSchemaManager: SandboxSchemaManager,
    @Value("\${sandbox.session.ttl:PT1H}") private val ttl: Duration
) {

    @Transactional
    fun reactivate(sessionKey: String, userId: UUID): SandboxSetupResponse {
        val session = sandboxSessionRepository.findBySessionKey(sessionKey)
            ?: throw BusinessException(SandboxErrorCode.SANDBOX_SESSION_NOT_FOUND)

        if (!session.isOwnedBy(userId.toString())) throw BusinessException(SandboxErrorCode.SANDBOX_FORBIDDEN)

        if (session.isActive()) {
            return SandboxSetupResponse(session.sessionKey, session.schemaName, session.extractedSql, session.expiresAt)
        }

        val newExpiresAt = LocalDateTime.now().plus(ttl)
        sandboxSchemaManager.setupSchema(session.schemaName, session.extractedSql)
        sandboxSessionRepository.reactivate(session.id!!, newExpiresAt)

        return SandboxSetupResponse(session.sessionKey, session.schemaName, session.extractedSql, newExpiresAt)
    }
}
