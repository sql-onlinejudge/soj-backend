package me.suhyun.soj.domain.grading.exception

import me.suhyun.soj.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class GradingErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {
    GRADING_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "채점 서버에 연결할 수 없습니다"),
}
