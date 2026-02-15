package me.suhyun.soj.domain.problem.application.service

import me.suhyun.soj.domain.problem.application.event.ProblemEventPublisher
import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemSearchService
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.global.infrastructure.cache.CacheService
import me.suhyun.soj.global.infrastructure.cache.config.CacheProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProblemServiceFindAllTest {

    @Mock
    private lateinit var problemRepository: ProblemRepository

    @Mock
    private lateinit var testCaseRepository: TestCaseRepository

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    @Mock
    private lateinit var cacheService: CacheService

    @Mock
    private lateinit var problemEventPublisher: ProblemEventPublisher

    @Mock
    private lateinit var problemSearchService: ProblemSearchService

    private val cacheProperties = CacheProperties()

    private lateinit var problemService: ProblemService

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        problemService = ProblemService(
            problemRepository,
            testCaseRepository,
            submissionRepository,
            cacheService,
            cacheProperties,
            problemEventPublisher,
            problemSearchService
        )
    }

    private fun createProblem(id: Long, title: String = "Problem $id", difficulty: Int = 3): Problem {
        return Problem(
            id = id,
            title = title,
            description = "Description",
            schemaSql = "CREATE TABLE test (id INT)",
            schemaMetadata = null,
            difficulty = difficulty,
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
    fun `should return paginated problems`() {
        val problems = listOf(createProblem(1L), createProblem(2L))
        val sort = listOf("createdAt:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, null, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(null, null, null, null, userId)).thenReturn(2L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L, 2L), userId)).thenReturn(emptyMap())

        val result = problemService.findAll(0, 10, null, null, null, sort, userId)

        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2L)
        assertThat(result.page).isEqualTo(0)
        assertThat(result.size).isEqualTo(10)
    }

    @Test
    fun `should filter by keyword`() {
        val problems = listOf(createProblem(1L, "SQL Basics"))
        val sort = listOf("createdAt:desc")

        whenever(problemSearchService.search("SQL")).thenReturn(listOf(1L))
        whenever(problemRepository.findByIdsWithFilters(listOf(1L), null, null, null, userId, sort)).thenReturn(problems)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(emptyMap())

        val result = problemService.findAll(0, 10, null, null, "SQL", sort, userId)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].title).isEqualTo("SQL Basics")
    }

    @Test
    fun `should filter by difficulty`() {
        val problems = listOf(createProblem(1L, difficulty = 5))
        val sort = listOf("createdAt:desc")

        whenever(problemRepository.findAll(0, 10, 5, null, null, sort, null, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(5, null, null, null, userId)).thenReturn(1L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(emptyMap())

        val result = problemService.findAll(0, 10, 5, null, null, sort, userId)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].difficulty).isEqualTo(5)
    }

    @Test
    fun `should calculate trial status NOT_ATTEMPTED`() {
        val problems = listOf(createProblem(1L))
        val sort = listOf("createdAt:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, null, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(null, null, null, null, userId)).thenReturn(1L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(emptyMap())

        val result = problemService.findAll(0, 10, null, null, null, sort, userId)

        assertThat(result.content[0].trialStatus).isEqualTo(TrialStatus.NOT_ATTEMPTED)
    }

    @Test
    fun `should calculate trial status ATTEMPTED`() {
        val problems = listOf(createProblem(1L))
        val sort = listOf("createdAt:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, null, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(null, null, null, null, userId)).thenReturn(1L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(mapOf(1L to false))

        val result = problemService.findAll(0, 10, null, null, null, sort, userId)

        assertThat(result.content[0].trialStatus).isEqualTo(TrialStatus.ATTEMPTED)
    }

    @Test
    fun `should calculate trial status SOLVED`() {
        val problems = listOf(createProblem(1L))
        val sort = listOf("createdAt:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, null, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(null, null, null, null, userId)).thenReturn(1L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(mapOf(1L to true))

        val result = problemService.findAll(0, 10, null, null, null, sort, userId)

        assertThat(result.content[0].trialStatus).isEqualTo(TrialStatus.SOLVED)
    }

    @Test
    fun `should return empty content when no problems exist`() {
        val sort = listOf("createdAt:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, null, userId)).thenReturn(emptyList())
        whenever(problemRepository.countAll(null, null, null, null, userId)).thenReturn(0L)

        val result = problemService.findAll(0, 10, null, null, null, sort, userId)

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0L)
    }

    @Test
    fun `should filter by trial status SOLVED`() {
        val problems = listOf(createProblem(1L))
        val sort = listOf("id:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, TrialStatus.SOLVED, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(null, null, null, TrialStatus.SOLVED, userId)).thenReturn(1L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(mapOf(1L to true))

        val result = problemService.findAll(0, 10, null, null, null, sort, userId, TrialStatus.SOLVED)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].trialStatus).isEqualTo(TrialStatus.SOLVED)
    }

    @Test
    fun `should filter by trial status ATTEMPTED`() {
        val problems = listOf(createProblem(1L))
        val sort = listOf("id:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, TrialStatus.ATTEMPTED, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(null, null, null, TrialStatus.ATTEMPTED, userId)).thenReturn(1L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(mapOf(1L to false))

        val result = problemService.findAll(0, 10, null, null, null, sort, userId, TrialStatus.ATTEMPTED)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].trialStatus).isEqualTo(TrialStatus.ATTEMPTED)
    }

    @Test
    fun `should filter by trial status NOT_ATTEMPTED`() {
        val problems = listOf(createProblem(1L))
        val sort = listOf("id:desc")

        whenever(problemRepository.findAll(0, 10, null, null, null, sort, TrialStatus.NOT_ATTEMPTED, userId)).thenReturn(problems)
        whenever(problemRepository.countAll(null, null, null, TrialStatus.NOT_ATTEMPTED, userId)).thenReturn(1L)
        whenever(submissionRepository.getTrialStatuses(listOf(1L), userId)).thenReturn(emptyMap())

        val result = problemService.findAll(0, 10, null, null, null, sort, userId, TrialStatus.NOT_ATTEMPTED)

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].trialStatus).isEqualTo(TrialStatus.NOT_ATTEMPTED)
    }
}
