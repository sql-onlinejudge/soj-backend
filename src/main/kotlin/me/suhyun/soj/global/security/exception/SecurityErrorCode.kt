package me.suhyun.soj.global.security.exception

import me.suhyun.soj.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class SecurityErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication required"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid or expired token"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied")
}
