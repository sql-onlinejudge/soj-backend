package me.suhyun.soj.domain.payment.domain.repository

import me.suhyun.soj.domain.payment.domain.model.SubscriptionPlan

interface SubscriptionPlanRepository {
    fun findActiveByName(name: String): SubscriptionPlan?
}
