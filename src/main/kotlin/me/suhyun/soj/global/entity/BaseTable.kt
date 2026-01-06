package me.suhyun.soj.global.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

abstract class BaseTable(name: String) : LongIdTable(name) {
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()
    val deletedAt = datetime("deleted_at").nullable()
}
