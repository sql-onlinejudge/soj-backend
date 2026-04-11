package me.suhyun.soj.domain.sandbox.application.service

import me.suhyun.soj.domain.sandbox.domain.model.SandboxSession
import me.suhyun.soj.domain.sandbox.domain.repository.SandboxSessionRepository
import me.suhyun.soj.domain.sandbox.exception.SandboxErrorCode
import me.suhyun.soj.domain.sandbox.infrastructure.SandboxSchemaManager
import me.suhyun.soj.domain.sandbox.infrastructure.SandboxSqlValidator
import me.suhyun.soj.domain.sandbox.presentation.response.SandboxSetupResponse
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Service
class SandboxSetupService(
    private val ocrService: OcrService,
    private val sandboxSqlValidator: SandboxSqlValidator,
    private val sandboxSchemaManager: SandboxSchemaManager,
    private val sandboxSessionRepository: SandboxSessionRepository,
    @Value("\${sandbox.session.ttl:PT1H}") private val ttl: Duration
) {

    @Transactional
    fun setup(imageBytes: ByteArray, mediaType: String, userId: UUID?): SandboxSetupResponse {
        validateMediaType(mediaType)

        val extractedSql = ocrService.extractSql(imageBytes, mediaType)
        sandboxSqlValidator.validateSetupSql(extractedSql)

        val sessionKey = UUID.randomUUID().toString()
        val schemaName = "sandbox_${sessionKey.replace("-", "").take(16)}"

        sandboxSchemaManager.setupSchema(schemaName, extractedSql)

        val session = sandboxSessionRepository.save(
            SandboxSession(
                sessionKey = sessionKey,
                userId = userId?.toString(),
                schemaName = schemaName,
                extractedSql = extractedSql,
                expiresAt = LocalDateTime.now().plus(ttl)
            )
        )

        return SandboxSetupResponse(
            sessionKey = session.sessionKey,
            schemaName = session.schemaName,
            extractedSql = session.extractedSql,
            expiresAt = session.expiresAt
        )
    }

    private fun validateMediaType(mediaType: String) {
        val supported = setOf("image/jpeg", "image/png", "image/gif", "image/webp")
        if (mediaType !in supported) throw BusinessException(SandboxErrorCode.UNSUPPORTED_IMAGE_TYPE)
    }
}
