package me.suhyun.soj.domain.sandbox.application.service

import me.suhyun.soj.domain.sandbox.domain.repository.SandboxSessionRepository
import me.suhyun.soj.domain.sandbox.exception.SandboxErrorCode
import me.suhyun.soj.domain.sandbox.infrastructure.SandboxSchemaManager
import me.suhyun.soj.domain.sandbox.infrastructure.SandboxSqlValidator
import me.suhyun.soj.domain.sandbox.presentation.response.SandboxQueryResponse
import me.suhyun.soj.domain.sandbox.presentation.response.SandboxSessionResponse
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SandboxQueryService(
    private val sandboxSessionRepository: SandboxSessionRepository,
    private val sandboxSqlValidator: SandboxSqlValidator,
    private val sandboxSchemaManager: SandboxSchemaManager,
    @Value("\${sandbox.session.query-timeout-ms:5000}") private val queryTimeoutMs: Int
) {

    @Transactional(readOnly = true)
    fun executeQuery(sessionKey: String, query: String, userId: UUID): SandboxQueryResponse {
        val session = sandboxSessionRepository.findBySessionKey(sessionKey)
            ?: throw BusinessException(SandboxErrorCode.SANDBOX_SESSION_NOT_FOUND)

        if (session.isExpired()) throw BusinessException(SandboxErrorCode.SANDBOX_SESSION_EXPIRED)
        if (!session.isOwnedBy(userId.toString())) throw BusinessException(SandboxErrorCode.SANDBOX_FORBIDDEN)

        sandboxSqlValidator.validateSelectQuery(query)

        val result = sandboxSchemaManager.executeQuery(session.schemaName, query, queryTimeoutMs)
        return SandboxQueryResponse(columns = result.columns, rows = result.rows)
    }

    @Transactional(readOnly = true)
    fun getSession(sessionKey: String, userId: UUID): SandboxSessionResponse {
        val session = sandboxSessionRepository.findBySessionKey(sessionKey)
            ?: throw BusinessException(SandboxErrorCode.SANDBOX_SESSION_NOT_FOUND)

        if (!session.isOwnedBy(userId.toString())) throw BusinessException(SandboxErrorCode.SANDBOX_FORBIDDEN)

        return SandboxSessionResponse(
            sessionKey = session.sessionKey,
            schemaName = session.schemaName,
            extractedSql = session.extractedSql,
            expiresAt = session.expiresAt,
            expired = session.isExpired()
        )
    }
}
