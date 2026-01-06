package me.suhyun.soj.domain.problem.domain

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ProblemEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ProblemEntity>(ProblemTable)

    var title by ProblemTable.title
    var description by ProblemTable.description
    var schemaSql by ProblemTable.schemaSql
    var difficulty by ProblemTable.difficulty
    var solvedCount by ProblemTable.solvedCount
    var submissionCount by ProblemTable.submissionCount
    var createdAt by ProblemTable.createdAt
    var updatedAt by ProblemTable.updatedAt
    var deletedAt by ProblemTable.deletedAt
}
