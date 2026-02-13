package me.suhyun.soj.domain.workbook.domain.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class WorkbookEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<WorkbookEntity>(WorkbookTable)

    var name by WorkbookTable.name
    var description by WorkbookTable.description
    var difficulty by WorkbookTable.difficulty
    var createdAt by WorkbookTable.createdAt
    var updatedAt by WorkbookTable.updatedAt
    var deletedAt by WorkbookTable.deletedAt
}
