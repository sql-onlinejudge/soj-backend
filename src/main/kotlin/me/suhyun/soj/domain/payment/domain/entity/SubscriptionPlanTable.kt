package me.suhyun.soj.domain.payment.domain.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object SubscriptionPlanTable : LongIdTable("subscription_plans") {
    val name = varchar("name", 50).uniqueIndex()
    val price = integer("price")
    val durationMonths = integer("duration_months")
    val isActive = bool("is_active")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()
}
