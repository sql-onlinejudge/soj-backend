package me.suhyun.soj.domain.problem.application.service

import me.suhyun.soj.domain.problem.domain.model.ColumnMetadata
import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.SchemaMetadata
import me.suhyun.soj.domain.problem.domain.model.TableMetadata
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.problem.presentation.request.UpdateProblemRequest
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.global.infrastructure.cache.CacheService
import me.suhyun.soj.global.infrastructure.cache.config.CacheProperties
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
class ProblemServiceUpdateTest {

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

    private val testSchemaMetadata = SchemaMetadata(
        tables = listOf(
            TableMetadata(
                name = "updated",
                columns = listOf(ColumnMetadata(name = "id", type = "INT", constraints = emptyList()))
            )
        )
    )

    @BeforeEach
    fun setUp() {
        problemService = ProblemService(problemRepository, testCaseRepository, submissionRepository, cacheService, cacheProperties)
    }

    @Test
    fun `should update problem successfully`() {
        val request = UpdateProblemRequest(
            title = "Updated Title",
            description = "Updated Description",
            schemaMetadata = testSchemaMetadata,
            difficulty = 5,
            timeLimit = 2000,
            isOrderSensitive = true
        )

        val updatedProblem = Problem(
            id = 1L,
            title = "Updated Title",
            description = "Updated Description",
            schemaSql = "CREATE TABLE updated (id INT);",
            schemaMetadata = testSchemaMetadata,
            difficulty = 5,
            timeLimit = 2000,
            isOrderSensitive = true,
            solvedCount = 0,
            submissionCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            deletedAt = null
        )

        whenever(
            problemRepository.update(
                id = eq(1L),
                title = eq(request.title),
                description = eq(request.description),
                schemaSql = any(),
                schemaMetadata = eq(request.schemaMetadata),
                difficulty = eq(request.difficulty),
                timeLimit = eq(request.timeLimit),
                isOrderSensitive = eq(request.isOrderSensitive)
            )
        ).thenReturn(updatedProblem)

        problemService.update(1L, request)

        verify(problemRepository).update(
            id = eq(1L),
            title = eq(request.title),
            description = eq(request.description),
            schemaSql = any(),
            schemaMetadata = eq(request.schemaMetadata),
            difficulty = eq(request.difficulty),
            timeLimit = eq(request.timeLimit),
            isOrderSensitive = eq(request.isOrderSensitive)
        )
    }

    @Test
    fun `should update only provided fields (partial update)`() {
        val request = UpdateProblemRequest(
            title = "Only Title Updated",
            description = null,
            schemaMetadata = null,
            difficulty = null,
            timeLimit = null,
            isOrderSensitive = null
        )

        val updatedProblem = Problem(
            id = 1L,
            title = "Only Title Updated",
            description = "Original Description",
            schemaSql = "CREATE TABLE test (id INT)",
            schemaMetadata = null,
            difficulty = 3,
            timeLimit = 1000,
            isOrderSensitive = false,
            solvedCount = 0,
            submissionCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            deletedAt = null
        )

        whenever(
            problemRepository.update(
                id = eq(1L),
                title = eq(request.title),
                description = eq(null),
                schemaSql = eq(null),
                schemaMetadata = eq(null),
                difficulty = eq(null),
                timeLimit = eq(null),
                isOrderSensitive = eq(null)
            )
        ).thenReturn(updatedProblem)

        problemService.update(1L, request)

        verify(problemRepository).update(
            id = eq(1L),
            title = eq("Only Title Updated"),
            description = eq(null),
            schemaSql = eq(null),
            schemaMetadata = eq(null),
            difficulty = eq(null),
            timeLimit = eq(null),
            isOrderSensitive = eq(null)
        )
    }

    @Test
    fun `should throw PROBLEM_NOT_FOUND when problem does not exist`() {
        val request = UpdateProblemRequest(title = "Updated Title")

        whenever(
            problemRepository.update(
                id = eq(999L),
                title = eq(request.title),
                description = eq(null),
                schemaSql = eq(null),
                schemaMetadata = eq(null),
                difficulty = eq(null),
                timeLimit = eq(null),
                isOrderSensitive = eq(null)
            )
        ).thenReturn(null)

        assertThatThrownBy { problemService.update(999L, request) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(ProblemErrorCode.PROBLEM_NOT_FOUND)
    }
}
