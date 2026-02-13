package me.suhyun.soj.domain.problem.application.service

import me.suhyun.soj.domain.problem.domain.model.ColumnMetadata
import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.SchemaMetadata
import me.suhyun.soj.domain.problem.domain.model.TableMetadata
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.presentation.request.CreateProblemRequest
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.global.infrastructure.cache.CacheService
import me.suhyun.soj.global.infrastructure.cache.config.CacheProperties
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata
import me.suhyun.soj.domain.testcase.domain.model.InsertStatement
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.domain.testcase.presentation.request.CreateTestCaseRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ProblemServiceCreateTest {

    @Mock
    private lateinit var problemRepository: ProblemRepository

    @Mock
    private lateinit var testCaseRepository: TestCaseRepository

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    @Mock
    private lateinit var cacheService: CacheService

    private val cacheProperties = CacheProperties()

    private lateinit var problemService: ProblemService

    private val testSchemaMetadata = SchemaMetadata(
        tables = listOf(
            TableMetadata(
                name = "test",
                columns = listOf(ColumnMetadata(name = "id", type = "INT", constraints = emptyList()))
            )
        )
    )

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
        problemService = ProblemService(problemRepository, testCaseRepository, submissionRepository, cacheService, cacheProperties)
    }

    @Test
    fun `should create problem with testcases successfully`() {
        val request = CreateProblemRequest(
            title = "Test Problem",
            description = "Test Description",
            schemaMetadata = testSchemaMetadata,
            difficulty = 3,
            timeLimit = 1000,
            isOrderSensitive = false,
            testcases = listOf(
                CreateTestCaseRequest(initData = testInitData, answerData = testAnswerData, isVisible = true)
            )
        )

        val savedProblem = Problem(
            id = 1L,
            title = request.title,
            description = request.description,
            schemaSql = "CREATE TABLE test (id INT);",
            schemaMetadata = request.schemaMetadata,
            difficulty = request.difficulty,
            timeLimit = request.timeLimit,
            isOrderSensitive = request.isOrderSensitive,
            solvedCount = 0,
            submissionCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )

        whenever(problemRepository.save(any())).thenReturn(savedProblem)
        whenever(testCaseRepository.save(any())).thenReturn(
            TestCase(
                id = 1L,
                problemId = 1L,
                initSql = "INSERT INTO test (id) VALUES (1);",
                initMetadata = testInitData,
                answer = "id\n1",
                answerMetadata = testAnswerData,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )

        problemService.create(request)

        verify(problemRepository).save(any())
        verify(testCaseRepository).save(any())
    }

    @Test
    fun `should save all testcases when creating problem`() {
        val testcases = listOf(
            CreateTestCaseRequest(initData = testInitData, answerData = testAnswerData, isVisible = true),
            CreateTestCaseRequest(initData = testInitData, answerData = testAnswerData, isVisible = true),
            CreateTestCaseRequest(initData = null, answerData = testAnswerData, isVisible = true)
        )

        val request = CreateProblemRequest(
            title = "Test Problem",
            description = "Test Description",
            schemaMetadata = testSchemaMetadata,
            difficulty = 2,
            timeLimit = 500,
            isOrderSensitive = true,
            testcases = testcases
        )

        val savedProblem = Problem(
            id = 1L,
            title = request.title,
            description = request.description,
            schemaSql = "CREATE TABLE test (id INT);",
            schemaMetadata = request.schemaMetadata,
            difficulty = request.difficulty,
            timeLimit = request.timeLimit,
            isOrderSensitive = request.isOrderSensitive,
            solvedCount = 0,
            submissionCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )

        whenever(problemRepository.save(any())).thenReturn(savedProblem)
        whenever(testCaseRepository.save(any())).thenReturn(
            TestCase(
                id = 1L,
                problemId = 1L,
                initSql = null,
                initMetadata = null,
                answer = "id\n1",
                answerMetadata = testAnswerData,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )

        problemService.create(request)

        verify(problemRepository).save(any())
        verify(testCaseRepository, times(3)).save(any())
    }
}
