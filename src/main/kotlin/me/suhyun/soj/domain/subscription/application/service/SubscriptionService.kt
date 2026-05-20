package me.suhyun.soj.domain.subscription.application.service

import me.suhyun.soj.domain.subscription.domain.model.Subscription
import me.suhyun.soj.domain.subscription.domain.model.enums.SubscriptionStatus
import me.suhyun.soj.domain.subscription.domain.repository.SubscriptionRepository
import me.suhyun.soj.domain.subscription.exception.SubscriptionErrorCode
import me.suhyun.soj.global.exception.BusinessException
import me.suhyun.soj.global.infrastructure.cache.CacheKeys
import me.suhyun.soj.global.infrastructure.cache.CacheService
import me.suhyun.soj.global.infrastructure.cache.config.CacheProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val cacheService: CacheService,
    private val cacheProperties: CacheProperties
) {

    fun findActiveByUserId(userId: UUID): Subscription? =
        subscriptionRepository.findValidByUserId(userId)

    fun isActive(userId: UUID): Boolean {
        val cached = cacheService.get(CacheKeys.Subscription.byUserId(userId), Boolean::class.java)
        if (cached != null) return cached

        val active = subscriptionRepository.findValidByUserId(userId) != null
        cacheService.put(CacheKeys.Subscription.byUserId(userId), active, cacheProperties.ttl.subscription)
        return active
    }

    @Transactional
    fun cancel(userId: UUID) {
        val subscription = subscriptionRepository.findActiveByUserId(userId)
            ?: throw BusinessException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND)
        subscriptionRepository.updateStatus(subscription.id!!, SubscriptionStatus.CANCELLED)
        cacheService.evict(CacheKeys.Subscription.byUserId(userId))
    }

    @Transactional
    fun activate(userId: UUID, months: Int = 1): Subscription {
        val now = LocalDateTime.now()
        val subscription = subscriptionRepository.save(
            Subscription(
                id = null,
                userId = userId,
                status = SubscriptionStatus.ACTIVE,
                startedAt = now,
                expiresAt = now.plusMonths(months.toLong()),
                createdAt = now,
                updatedAt = null
            )
        )
        cacheService.evict(CacheKeys.Subscription.byUserId(userId))
        return subscription
    }
}
