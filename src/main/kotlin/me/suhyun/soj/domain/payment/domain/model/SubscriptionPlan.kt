package me.suhyun.soj.domain.payment.domain.model

import me.suhyun.soj.domain.payment.domain.entity.SubscriptionPlanEntity
import java.time.LocalDateTime

data class SubscriptionPlan(
    val id: Long,
    val name: String,
    val price: Int,
    val durationMonths: Int,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(entity: SubscriptionPlanEntity) = SubscriptionPlan(
            id = entity.id.value,
            name = entity.name,
            price = entity.price,
            durationMonths = entity.durationMonths,
            isActive = entity.isActive,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
