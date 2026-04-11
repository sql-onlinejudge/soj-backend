package me.suhyun.soj.domain.sandbox.domain.entity

import me.suhyun.soj.domain.sandbox.domain.model.SandboxSession
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SandboxSessionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SandboxSessionEntity>(SandboxSessionTable) {
        fun from(entity: SandboxSessionEntity) = SandboxSession(
            id = entity.id.value,
            sessionKey = entity.sessionKey,
            userId = entity.userId,
            schemaName = entity.schemaName,
            extractedSql = entity.extractedSql,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    var sessionKey by SandboxSessionTable.sessionKey
    var userId by SandboxSessionTable.userId
    var schemaName by SandboxSessionTable.dbSchema
    var extractedSql by SandboxSessionTable.extractedSql
    var expiresAt by SandboxSessionTable.expiresAt
    var createdAt by SandboxSessionTable.createdAt
    var updatedAt by SandboxSessionTable.updatedAt
    var deletedAt by SandboxSessionTable.deletedAt
}
