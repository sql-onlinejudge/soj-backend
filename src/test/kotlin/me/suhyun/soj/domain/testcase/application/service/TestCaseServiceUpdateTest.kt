package me.suhyun.soj.domain.testcase.application.service

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata
import me.suhyun.soj.domain.testcase.domain.model.InsertStatement
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.domain.testcase.exception.TestCaseErrorCode
import me.suhyun.soj.domain.testcase.presentation.request.UpdateTestCaseRequest
import me.suhyun.soj.global.exception.BusinessException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TestCaseServiceUpdateTest {

    @Mock
    private lateinit var testCaseRepository: TestCaseRepository

    @Mock
    private lateinit var problemRepository: ProblemRepository

    private lateinit var testCaseService: TestCaseService

    private val testInitData = InitMetadata(
        statements = listOf(
            InsertStatement(table = "test", rows = listOf(mapOf("id" to 2)))
        )
    )

    private val testAnswerData = AnswerMetadata(
        columns = listOf("id"),
        rows = listOf(listOf(2))
    )

    @BeforeEach
    fun setUp() {
        testCaseService = TestCaseService(testCaseRepository, problemRepository)
    }

    private fun createTestCase(id: Long, problemId: Long): TestCase {
        return TestCase(
            id = id,
            problemId = problemId,
            initSql = "INSERT INTO test VALUES (1)",
            initMetadata = null,
            answer = "1",
            answerMetadata = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }

    @Test
    fun `should update testcase successfully`() {
        val problemId = 1L
        val testcaseId = 1L
        val request = UpdateTestCaseRequest(
            initData = testInitData,
            answerData = testAnswerData
        )

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, problemId))

        testCaseService.update(problemId, testcaseId, request)

        verify(testCaseRepository).update(eq(testcaseId), any(), eq(request.initData), any(), eq(request.answerData), eq(request.isVisible))
    }

    @Test
    fun `should update only initData (partial update)`() {
        val problemId = 1L
        val testcaseId = 1L
        val request = UpdateTestCaseRequest(
            initData = testInitData,
            answerData = null
        )

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, problemId))

        testCaseService.update(problemId, testcaseId, request)

        verify(testCaseRepository).update(eq(testcaseId), any(), eq(testInitData), eq(null), eq(null), eq(null))
    }

    @Test
    fun `should update only answerData (partial update)`() {
        val problemId = 1L
        val testcaseId = 1L
        val request = UpdateTestCaseRequest(
            initData = null,
            answerData = testAnswerData
        )

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, problemId))

        testCaseService.update(problemId, testcaseId, request)

        verify(testCaseRepository).update(eq(testcaseId), eq(null), eq(null), any(), eq(testAnswerData), eq(null))
    }

    @Test
    fun `should throw TEST_CASE_NOT_FOUND when testcase does not exist`() {
        val problemId = 1L
        val testcaseId = 999L
        val request = UpdateTestCaseRequest(initData = testInitData, answerData = testAnswerData)

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(null)

        assertThatThrownBy { testCaseService.update(problemId, testcaseId, request) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
    }

    @Test
    fun `should throw TEST_CASE_NOT_FOUND when problemId mismatch`() {
        val problemId = 1L
        val differentProblemId = 2L
        val testcaseId = 1L
        val request = UpdateTestCaseRequest(initData = testInitData, answerData = testAnswerData)

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, differentProblemId))

        assertThatThrownBy { testCaseService.update(problemId, testcaseId, request) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
    }
}
