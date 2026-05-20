package me.suhyun.soj.domain.payment.exception

import me.suhyun.soj.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class PaymentErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
    PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "결제 승인에 실패했습니다."),
    SUBSCRIPTION_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "구독 플랜을 찾을 수 없습니다.")
}
