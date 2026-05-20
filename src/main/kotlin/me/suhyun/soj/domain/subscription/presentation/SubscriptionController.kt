package me.suhyun.soj.domain.subscription.presentation

import me.suhyun.soj.domain.subscription.application.service.SubscriptionService
import me.suhyun.soj.domain.subscription.exception.SubscriptionErrorCode
import me.suhyun.soj.domain.subscription.presentation.response.SubscriptionResponse
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/subscriptions")
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {

    @GetMapping("/me")
    fun getMySubscription(): SubscriptionResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID
        val subscription = subscriptionService.findActiveByUserId(userId)
            ?: throw BusinessException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND)
        return SubscriptionResponse.from(subscription)
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelMySubscription() {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID
        subscriptionService.cancel(userId)
    }
}
