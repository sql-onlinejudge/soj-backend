package me.suhyun.soj.domain.testcase.domain.repository

import me.suhyun.soj.domain.testcase.domain.entity.TestCaseEntity
import me.suhyun.soj.domain.testcase.domain.entity.TestCaseTable
import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TestCaseRepositoryImpl : TestCaseRepository {

    override fun save(testCase: TestCase): TestCase {
        val entity = TestCaseEntity.new {
            this.problemId = testCase.problemId
            this.initSql = testCase.initSql
            this.initMetadata = testCase.initMetadata
            this.answer = testCase.answer
            this.answerMetadata = testCase.answerMetadata
            this.isVisible = testCase.isVisible ?: true
            this.createdAt = LocalDateTime.now()
        }
        return TestCase.from(entity)
    }

    override fun findById(id: Long): TestCase? {
        return TestCaseEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?.let { TestCase.from(it) }
    }

    override fun findAllByProblemId(problemId: Long, isVisible: Boolean?): List<TestCase> {
        val query = TestCaseTable.selectAll()
            .andWhere { TestCaseTable.deletedAt.isNull() }
            .andWhere { TestCaseTable.problemId eq problemId }
        isVisible?.let { query.andWhere { TestCaseTable.isVisible eq it } }
        return query
            .map { TestCaseEntity.wrapRow(it) }
            .map { TestCase.from(it) }
    }

    override fun update(
        id: Long,
        initSql: String?,
        initMetadata: InitMetadata?,
        answer: String?,
        answerMetadata: AnswerMetadata?,
        isVisible: Boolean?
    ): TestCase? {
        val entity = TestCaseEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?: return null

        initSql?.let {
            entity.initSql = it
            entity.initMetadata = initMetadata
        }
        answer?.let {
            entity.answer = it
            entity.answerMetadata = answerMetadata
        }
        isVisible?.let { entity.isVisible = it }
        entity.updatedAt = LocalDateTime.now()

        return TestCase.from(entity)
    }

    override fun softDelete(id: Long): Boolean {
        val entity = TestCaseEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?: return false

        entity.deletedAt = LocalDateTime.now()
        return true
    }
}
