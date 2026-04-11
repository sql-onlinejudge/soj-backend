package me.suhyun.soj.domain.sandbox.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import me.suhyun.soj.domain.sandbox.exception.SandboxErrorCode
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.Base64

@Service
class OcrService(
    @Qualifier("anthropicWebClient")
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) {

    fun extractSql(imageBytes: ByteArray, mediaType: String): String {
        val base64Image = Base64.getEncoder().encodeToString(imageBytes)
        val requestBody = mapOf(
            "model" to "claude-haiku-4-5-20251001",
            "max_tokens" to 4096,
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to listOf(
                        mapOf(
                            "type" to "image",
                            "source" to mapOf(
                                "type" to "base64",
                                "media_type" to mediaType,
                                "data" to base64Image
                            )
                        ),
                        mapOf(
                            "type" to "text",
                            "text" to """
                                이 이미지는 SQL 자격증 시험 문제지입니다.
                                이미지에서 테이블 구조와 데이터를 분석하여 SQL DDL(CREATE TABLE)과 데이터 삽입(INSERT INTO) 구문만 추출해 주세요.
                                다른 설명 없이 SQL 구문만 출력하세요. 여러 테이블과 INSERT가 있으면 모두 포함하세요.
                            """.trimIndent()
                        )
                    )
                )
            )
        )

        val responseBody = webClient.post()
            .uri("/v1/messages")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String::class.java)
            .block() ?: throw BusinessException(SandboxErrorCode.OCR_EXTRACTION_FAILED)

        return parseSqlFromResponse(responseBody)
    }

    private fun parseSqlFromResponse(responseBody: String): String {
        val root = objectMapper.readTree(responseBody)
        val text = root.path("content").path(0).path("text").asText("")
        if (text.isBlank()) throw BusinessException(SandboxErrorCode.OCR_EXTRACTION_FAILED)

        val codeBlockRegex = Regex("```(?:sql)?\\s*([\\s\\S]*?)```", RegexOption.IGNORE_CASE)
        val match = codeBlockRegex.find(text)
        val sql = if (match != null) match.groupValues[1].trim() else text.trim()

        if (sql.isBlank()) throw BusinessException(SandboxErrorCode.OCR_EXTRACTION_FAILED)
        return sql
    }
}
