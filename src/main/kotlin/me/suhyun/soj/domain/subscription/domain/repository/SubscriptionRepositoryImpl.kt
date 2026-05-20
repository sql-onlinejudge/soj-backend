package me.suhyun.soj.domain.subscription.domain.repository

import me.suhyun.soj.domain.subscription.domain.entity.SubscriptionEntity
import me.suhyun.soj.domain.subscription.domain.entity.SubscriptionTable
import me.suhyun.soj.domain.subscription.domain.model.Subscription
import me.suhyun.soj.domain.subscription.domain.model.enums.SubscriptionStatus
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class SubscriptionRepositoryImpl : SubscriptionRepository {

    override fun save(subscription: Subscription): Subscription {
        val entity = SubscriptionEntity.new {
            this.userId = subscription.userId.toString()
            this.status = subscription.status
            this.startedAt = subscription.startedAt
            this.expiresAt = subscription.expiresAt
            this.createdAt = LocalDateTime.now()
        }
        return Subscription.from(entity)
    }

    override fun findActiveByUserId(userId: UUID): Subscription? {
        return SubscriptionTable.selectAll()
            .where {
                (SubscriptionTable.userId eq userId.toString()) and
                (SubscriptionTable.status eq SubscriptionStatus.ACTIVE) and
                (SubscriptionTable.expiresAt greater LocalDateTime.now()) and
                SubscriptionTable.deletedAt.isNull()
            }
            .orderBy(SubscriptionTable.expiresAt to SortOrder.DESC)
            .firstOrNull()
            ?.let { Subscription.from(SubscriptionEntity.wrapRow(it)) }
    }

    override fun findValidByUserId(userId: UUID): Subscription? {
        return SubscriptionTable.selectAll()
            .where {
                (SubscriptionTable.userId eq userId.toString()) and
                (SubscriptionTable.status inList listOf(SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELLED)) and
                (SubscriptionTable.expiresAt greater LocalDateTime.now()) and
                SubscriptionTable.deletedAt.isNull()
            }
            .orderBy(SubscriptionTable.expiresAt to SortOrder.DESC)
            .firstOrNull()
            ?.let { Subscription.from(SubscriptionEntity.wrapRow(it)) }
    }

    override fun updateStatus(id: Long, status: SubscriptionStatus): Boolean {
        val entity = SubscriptionEntity.findById(id) ?: return false
        entity.status = status
        entity.updatedAt = LocalDateTime.now()
        return true
    }
}
