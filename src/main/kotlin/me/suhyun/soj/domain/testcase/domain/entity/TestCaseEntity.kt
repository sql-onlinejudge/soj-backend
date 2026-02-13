package me.suhyun.soj.domain.testcase.domain.entity

import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TestCaseEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TestCaseEntity>(TestCaseTable)

    var problemId by TestCaseTable.problemId
    var initSql by TestCaseTable.initSql
    var initMetadata: InitMetadata? by TestCaseTable.initMetadata
    var answer by TestCaseTable.answer
    var answerMetadata: AnswerMetadata? by TestCaseTable.answerMetadata
    var isVisible: Boolean by TestCaseTable.isVisible
    var createdAt by TestCaseTable.createdAt
    var updatedAt by TestCaseTable.updatedAt
    var deletedAt by TestCaseTable.deletedAt
}
