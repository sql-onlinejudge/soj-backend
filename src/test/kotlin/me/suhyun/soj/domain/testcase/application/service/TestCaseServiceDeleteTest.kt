package me.suhyun.soj.domain.testcase.application.service

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.domain.testcase.exception.TestCaseErrorCode
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
class TestCaseServiceDeleteTest {

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
    fun `should delete testcase successfully`() {
        val problemId = 1L
        val testcaseId = 1L

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, problemId))
        whenever(testCaseRepository.softDelete(testcaseId)).thenReturn(true)

        testCaseService.delete(problemId, testcaseId)

        verify(testCaseRepository).softDelete(testcaseId)
    }

    @Test
    fun `should throw TEST_CASE_NOT_FOUND when testcase does not exist`() {
        val problemId = 1L
        val testcaseId = 999L

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(null)

        assertThatThrownBy { testCaseService.delete(problemId, testcaseId) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
    }

    @Test
    fun `should throw TEST_CASE_NOT_FOUND when problemId mismatch`() {
        val problemId = 1L
        val differentProblemId = 2L
        val testcaseId = 1L

        whenever(testCaseRepository.findById(testcaseId)).thenReturn(createTestCase(testcaseId, differentProblemId))

        assertThatThrownBy { testCaseService.delete(problemId, testcaseId) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
    }
}
