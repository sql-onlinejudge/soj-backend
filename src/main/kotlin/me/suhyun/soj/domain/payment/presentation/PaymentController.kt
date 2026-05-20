package me.suhyun.soj.domain.payment.presentation

import jakarta.servlet.http.HttpServletResponse
import me.suhyun.soj.domain.payment.application.service.PaymentService
import me.suhyun.soj.domain.payment.presentation.response.CheckoutResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/payments")
class PaymentController(
    private val paymentService: PaymentService,
    @Value("\${app.frontend-url}") private val frontendUrl: String
) {

    @PostMapping("/checkout")
    fun checkout(): CheckoutResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID
        return paymentService.checkout(userId)
    }

    @GetMapping("/success")
    fun success(
        @RequestParam paymentKey: String,
        @RequestParam orderId: String,
        @RequestParam amount: Int,
        response: HttpServletResponse
    ) {
        paymentService.confirm(paymentKey, orderId, amount)
        response.sendRedirect("$frontendUrl?payment=success")
    }

    @GetMapping("/fail")
    fun fail(
        @RequestParam orderId: String,
        @RequestParam(required = false) message: String?,
        response: HttpServletResponse
    ) {
        paymentService.fail(orderId)
        response.sendRedirect("$frontendUrl?payment=fail")
    }
}
