package me.suhyun.soj.domain.payment.domain.repository

import me.suhyun.soj.domain.payment.domain.entity.SubscriptionPlanEntity
import me.suhyun.soj.domain.payment.domain.entity.SubscriptionPlanTable
import me.suhyun.soj.domain.payment.domain.model.SubscriptionPlan
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
class SubscriptionPlanRepositoryImpl : SubscriptionPlanRepository {

    override fun findActiveByName(name: String): SubscriptionPlan? {
        return SubscriptionPlanTable.selectAll()
            .where { (SubscriptionPlanTable.name eq name) and (SubscriptionPlanTable.isActive eq true) }
            .firstOrNull()
            ?.let { SubscriptionPlan.from(SubscriptionPlanEntity.wrapRow(it)) }
    }
}
