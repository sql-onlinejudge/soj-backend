package me.suhyun.soj.domain.subscription.domain.repository

import me.suhyun.soj.domain.subscription.domain.model.Subscription
import java.util.UUID

interface SubscriptionRepository {
    fun save(subscription: Subscription): Subscription
    fun findActiveByUserId(userId: UUID): Subscription?
    fun findValidByUserId(userId: UUID): Subscription?
    fun updateStatus(id: Long, status: me.suhyun.soj.domain.subscription.domain.model.enums.SubscriptionStatus): Boolean
}
