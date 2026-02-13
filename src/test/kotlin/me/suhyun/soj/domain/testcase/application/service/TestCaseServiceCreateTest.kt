package me.suhyun.soj.domain.testcase.application.service

import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata
import me.suhyun.soj.domain.testcase.domain.model.InsertStatement
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.domain.testcase.presentation.request.CreateTestCaseRequest
import me.suhyun.soj.global.exception.BusinessException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TestCaseServiceCreateTest {

    @Mock
    private lateinit var testCaseRepository: TestCaseRepository

    @Mock
    private lateinit var problemRepository: ProblemRepository

    private lateinit var testCaseService: TestCaseService

    private val testInitData = InitMetadata(
        statements = listOf(
            InsertStatement(table = "test", rows = listOf(mapOf("id" to 1)))
        )
    )

    private val testAnswerData = AnswerMetadata(
        columns = listOf("id"),
        rows = listOf(listOf(1))
    )

    @BeforeEach
    fun setUp() {
        testCaseService = TestCaseService(testCaseRepository, problemRepository)
    }

    private fun createProblem(id: Long): Problem {
        return Problem(
            id = id,
            title = "Test Problem",
            description = "Description",
            schemaSql = "CREATE TABLE test (id INT)",
            schemaMetadata = null,
            difficulty = 3,
            timeLimit = 1000,
            isOrderSensitive = false,
            solvedCount = 0,
            submissionCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }

    @Test
    fun `should create testcase successfully`() {
        val problemId = 1L
        val request = CreateTestCaseRequest(
            initData = testInitData,
            answerData = testAnswerData,
            isVisible = true
        )

        whenever(problemRepository.findById(problemId)).thenReturn(createProblem(problemId))
        whenever(testCaseRepository.save(any())).thenReturn(
            TestCase(
                id = 1L,
                problemId = problemId,
                initSql = "INSERT INTO test (id) VALUES (1);",
                initMetadata = request.initData,
                answer = "id\n1",
                answerMetadata = request.answerData,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )

        testCaseService.create(problemId, request)

        verify(problemRepository).findById(problemId)
        verify(testCaseRepository).save(any())
    }

    @Test
    fun `should create testcase with null initData`() {
        val problemId = 1L
        val request = CreateTestCaseRequest(
            initData = null,
            answerData = testAnswerData,
            isVisible = true
        )

        whenever(problemRepository.findById(problemId)).thenReturn(createProblem(problemId))
        whenever(testCaseRepository.save(any())).thenReturn(
            TestCase(
                id = 1L,
                problemId = problemId,
                initSql = null,
                initMetadata = null,
                answer = "id\n1",
                answerMetadata = request.answerData,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )

        testCaseService.create(problemId, request)

        verify(testCaseRepository).save(any())
    }

    @Test
    fun `should throw PROBLEM_NOT_FOUND when problem does not exist`() {
        val problemId = 999L
        val request = CreateTestCaseRequest(
            initData = testInitData,
            answerData = testAnswerData,
            isVisible = true
        )

        whenever(problemRepository.findById(problemId)).thenReturn(null)


        assertThatThrownBy { testCaseService.create(problemId, request) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(ProblemErrorCode.PROBLEM_NOT_FOUND)
    }
}
