package me.suhyun.soj.domain.problem.application.service

import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.global.infrastructure.cache.CacheService
import me.suhyun.soj.global.infrastructure.cache.config.CacheProperties
import me.suhyun.soj.global.exception.BusinessException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ProblemServiceFindByIdTest {

    @Mock
    private lateinit var problemRepository: ProblemRepository

    @Mock
    private lateinit var testCaseRepository: TestCaseRepository

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    @Mock
    private lateinit var cacheService: CacheService

    @Mock
    private lateinit var cacheProperties: CacheProperties

    private lateinit var problemService: ProblemService

    @BeforeEach
    fun setUp() {
        problemService = ProblemService(problemRepository, testCaseRepository, submissionRepository, cacheService, cacheProperties)
    }

    @Test
    fun `should return problem detail successfully`() {
        val problem = Problem(
            id = 1L,
            title = "Test Problem",
            description = "Test Description",
            schemaSql = "CREATE TABLE test (id INT)",
            schemaMetadata = null,
            difficulty = 3,
            timeLimit = 1000,
            isOrderSensitive = false,
            solvedCount = 5,
            submissionCount = 10,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            deletedAt = null
        )

        whenever(problemRepository.findById(1L)).thenReturn(problem)

        val result = problemService.findById(1L, null)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("Test Problem")
        assertThat(result.description).isEqualTo("Test Description")
        assertThat(result.schemaSql).isEqualTo("CREATE TABLE test (id INT)")
        assertThat(result.difficulty).isEqualTo(3)
        assertThat(result.timeLimit).isEqualTo(1000)
        assertThat(result.isOrderSensitive).isFalse()
        assertThat(result.solvedCount).isEqualTo(5)
        assertThat(result.submissionCount).isEqualTo(10)
    }

    @Test
    fun `should throw PROBLEM_NOT_FOUND when problem does not exist`() {
        whenever(problemRepository.findById(999L)).thenReturn(null)

        assertThatThrownBy { problemService.findById(999L, null) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(ProblemErrorCode.PROBLEM_NOT_FOUND)
    }
}
