package me.suhyun.soj.domain.payment.domain.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SubscriptionPlanEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SubscriptionPlanEntity>(SubscriptionPlanTable)
    var name by SubscriptionPlanTable.name
    var price by SubscriptionPlanTable.price
    var durationMonths by SubscriptionPlanTable.durationMonths
    var isActive by SubscriptionPlanTable.isActive
    var createdAt by SubscriptionPlanTable.createdAt
    var updatedAt by SubscriptionPlanTable.updatedAt
}
