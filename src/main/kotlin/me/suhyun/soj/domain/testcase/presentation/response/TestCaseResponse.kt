package me.suhyun.soj.domain.testcase.presentation.response

import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import java.time.LocalDateTime

data class TestCaseResponse(
    val id: Long,
    val problemId: Long,
    val initSql: String?,
    val initMetadata: InitMetadata?,
    val answer: String,
    val answerMetadata: AnswerMetadata?,
    val isVisible: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(testCase: TestCase): TestCaseResponse {
            return TestCaseResponse(
                id = testCase.id!!,
                problemId = testCase.problemId,
                initSql = testCase.initSql,
                initMetadata = testCase.initMetadata,
                answer = testCase.answer,
                answerMetadata = testCase.answerMetadata,
                isVisible = testCase.isVisible ?: true,
                createdAt = testCase.createdAt
            )
        }
    }
}
