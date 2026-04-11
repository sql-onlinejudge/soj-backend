package me.suhyun.soj.domain.sandbox.exception

import me.suhyun.soj.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class SandboxErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {
    SANDBOX_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "샌드박스 세션을 찾을 수 없습니다"),
    SANDBOX_SESSION_EXPIRED(HttpStatus.GONE, "샌드박스 세션이 만료되었습니다"),
    SANDBOX_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 세션만 접근할 수 있습니다"),
    FORBIDDEN_SETUP_SQL(HttpStatus.BAD_REQUEST, "CREATE TABLE과 INSERT INTO만 허용됩니다"),
    OCR_EXTRACTION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "이미지에서 SQL을 추출하지 못했습니다"),
    SANDBOX_SCHEMA_SETUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "샌드박스 스키마 설정에 실패했습니다"),
    UNSUPPORTED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다")
}
