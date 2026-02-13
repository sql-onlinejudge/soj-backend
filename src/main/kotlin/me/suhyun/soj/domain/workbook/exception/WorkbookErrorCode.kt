package me.suhyun.soj.domain.workbook.exception

import me.suhyun.soj.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class WorkbookErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    WORKBOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 문제집입니다."),
}
