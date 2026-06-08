package me.suhyun.soj.domain.subscription.domain.model

import me.suhyun.soj.domain.subscription.domain.entity.SubscriptionEntity
import me.suhyun.soj.domain.subscription.domain.model.enums.SubscriptionStatus
import java.time.LocalDateTime
import java.util.UUID

data class Subscription(
    val id: Long?,
    val userId: UUID,
    val status: SubscriptionStatus,
    val startedAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
) {
    companion object {
        fun from(entity: SubscriptionEntity) = Subscription(
            id = entity.id.value,
            userId = UUID.fromString(entity.userId),
            status = entity.status,
            startedAt = entity.startedAt,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }
}
