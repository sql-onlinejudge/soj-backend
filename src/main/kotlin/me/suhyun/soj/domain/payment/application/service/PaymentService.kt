package me.suhyun.soj.domain.payment.application.service

import me.suhyun.soj.domain.payment.domain.model.Payment
import me.suhyun.soj.domain.payment.domain.model.enums.PaymentStatus
import me.suhyun.soj.domain.payment.domain.repository.PaymentRepository
import me.suhyun.soj.domain.payment.domain.repository.SubscriptionPlanRepository
import me.suhyun.soj.domain.payment.exception.PaymentErrorCode
import me.suhyun.soj.domain.payment.infrastructure.toss.TossPaymentsClient
import me.suhyun.soj.domain.payment.infrastructure.toss.TossPaymentsProperties
import me.suhyun.soj.domain.payment.presentation.response.CheckoutResponse
import me.suhyun.soj.domain.subscription.application.service.SubscriptionService
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val subscriptionPlanRepository: SubscriptionPlanRepository,
    private val subscriptionService: SubscriptionService,
    private val tossPaymentsClient: TossPaymentsClient,
    private val tossProperties: TossPaymentsProperties
) {
    companion object {
        private const val PREMIUM_PLAN = "PREMIUM_MONTHLY"
    }

    fun checkout(userId: UUID): CheckoutResponse {
        val plan = subscriptionPlanRepository.findActiveByName(PREMIUM_PLAN)
            ?: throw BusinessException(PaymentErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND)
        val orderId = UUID.randomUUID().toString()
        paymentRepository.save(
            Payment(
                id = null,
                userId = userId,
                orderId = orderId,
                paymentKey = null,
                amount = plan.price,
                status = PaymentStatus.PENDING,
                subscriptionId = null,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        )
        return CheckoutResponse(
            orderId = orderId,
            amount = plan.price,
            clientKey = tossProperties.clientKey,
            successUrl = tossProperties.successUrl,
            failUrl = tossProperties.failUrl
        )
    }

    fun confirm(paymentKey: String, orderId: String, amount: Int) {
        val payment = paymentRepository.findByOrderId(orderId)
            ?: throw BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND)

        if (payment.status == PaymentStatus.DONE) return

        if (payment.amount != amount) {
            throw BusinessException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH)
        }

        tossPaymentsClient.confirm(paymentKey, orderId, amount)

        val subscription = subscriptionService.activate(payment.userId)
        paymentRepository.updateStatus(
            id = payment.id!!,
            status = PaymentStatus.DONE,
            paymentKey = paymentKey,
            subscriptionId = subscription.id
        )
    }

    fun fail(orderId: String) {
        val payment = paymentRepository.findByOrderId(orderId) ?: return
        if (payment.status == PaymentStatus.DONE) return
        paymentRepository.updateStatus(payment.id!!, PaymentStatus.FAILED)
    }
}
