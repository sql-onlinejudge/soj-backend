package me.suhyun.soj.domain.payment.infrastructure.toss

import me.suhyun.soj.domain.payment.exception.PaymentErrorCode
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.Base64

@Component
class TossPaymentsClient(private val properties: TossPaymentsProperties) {

    private val webClient = WebClient.builder()
        .baseUrl("https://api.tosspayments.com")
        .defaultHeader("Content-Type", "application/json")
        .build()

    fun confirm(paymentKey: String, orderId: String, amount: Int) {
        val encoded = Base64.getEncoder().encodeToString("${properties.secretKey}:".toByteArray())
        val body = mapOf("paymentKey" to paymentKey, "orderId" to orderId, "amount" to amount)

        try {
            webClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic $encoded")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        } catch (e: Exception) {
            throw BusinessException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED)
        }
    }
}
