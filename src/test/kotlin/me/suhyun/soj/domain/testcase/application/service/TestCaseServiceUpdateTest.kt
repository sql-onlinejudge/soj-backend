package me.suhyun.soj.domain.testcase.application.service

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
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

    @BeforeEach
    fun setUp() {
        testCaseService = TestCaseService(testCaseRepository, problemRepository)
    }

    private fun createTestCase(id: Long, problemId: Long): TestCase {
        return TestCase(
            id = id,
            problemId = problemId,
            initSql = "INSERT INTO test VALUES (1)",
            answer = "1",
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
            initSql = "INSERT INTO test VALUES (2)",
            answer = "2"
        )

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, problemId))

        testCaseService.update(problemId, testcaseId, request)

        verify(testCaseRepository).update(testcaseId, request.initSql, request.answer)
    }

    @Test
    fun `should update only initSql (partial update)`() {
        val problemId = 1L
        val testcaseId = 1L
        val request = UpdateTestCaseRequest(
            initSql = "INSERT INTO test VALUES (999)",
            answer = null
        )

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, problemId))

        testCaseService.update(problemId, testcaseId, request)

        verify(testCaseRepository).update(testcaseId, "INSERT INTO test VALUES (999)", null)
    }

    @Test
    fun `should update only answer (partial update)`() {
        val problemId = 1L
        val testcaseId = 1L
        val request = UpdateTestCaseRequest(
            initSql = null,
            answer = "new answer"
        )

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, problemId))

        testCaseService.update(problemId, testcaseId, request)

        verify(testCaseRepository).update(testcaseId, null, "new answer")
    }

    @Test
    fun `should throw TEST_CASE_NOT_FOUND when testcase does not exist`() {
        val problemId = 1L
        val testcaseId = 999L
        val request = UpdateTestCaseRequest(initSql = "new sql", answer = "new answer")

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
        val request = UpdateTestCaseRequest(initSql = "new sql", answer = "new answer")

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, differentProblemId))

        assertThatThrownBy { testCaseService.update(problemId, testcaseId, request) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
    }
}
