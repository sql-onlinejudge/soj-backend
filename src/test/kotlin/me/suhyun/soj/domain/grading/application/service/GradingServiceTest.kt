package me.suhyun.soj.domain.grading.application.service

import me.suhyun.soj.domain.grading.exception.QueryExecutionException
import me.suhyun.soj.domain.grading.exception.QueryTimeoutException
import me.suhyun.soj.domain.grading.infrastructure.QueryExecutor
import me.suhyun.soj.domain.grading.infrastructure.sse.SseEmitterService
import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.submission.domain.model.Submission
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.submission.exception.SubmissionErrorCode
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.global.exception.BusinessException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GradingServiceTest {

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    @Mock
    private lateinit var problemRepository: ProblemRepository

    @Mock
    private lateinit var testCaseRepository: TestCaseRepository

    @Mock
    private lateinit var queryExecutor: QueryExecutor

    @Mock
    private lateinit var resultComparator: ResultComparator

    @Mock
    private lateinit var sseEmitterService: SseEmitterService

    private lateinit var gradingService: GradingService

    private val submissionId = 1L
    private val problemId = 1L
    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        gradingService = GradingService(
            submissionRepository,
            problemRepository,
            testCaseRepository,
            queryExecutor,
            resultComparator,
            sseEmitterService
        )
    }

    private fun createSubmission(): Submission {
        return Submission(
            id = submissionId,
            problemId = problemId,
            userId = userId,
            query = "SELECT * FROM users",
            status = SubmissionStatus.PENDING,
            verdict = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }

    private fun createProblem(): Problem {
        return Problem(
            id = problemId,
            title = "Test Problem",
            description = "Description",
            schemaSql = "CREATE TABLE users (id INT, name VARCHAR(100))",
            difficulty = 3,
            timeLimit = 1000,
            isOrderSensitive = true,
            solvedCount = 0,
            submissionCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }

    private fun createTestCase(id: Long): TestCase {
        return TestCase(
            id = id,
            problemId = problemId,
            initSql = "INSERT INTO users VALUES (1, 'Alice')",
            answer = "1\tAlice",
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }

    @Nested
    inner class Grade {

        @Test
        fun `should return ACCEPTED when all testcases pass`() {
            val submission = createSubmission()
            val problem = createProblem()
            val testCases = listOf(createTestCase(1L), createTestCase(2L))

            whenever(submissionRepository.findById(submissionId)).thenReturn(submission)
            whenever(problemRepository.findById(problemId)).thenReturn(problem)
            whenever(testCaseRepository.findAllByProblemId(problemId)).thenReturn(testCases)
            whenever(submissionRepository.updateStatus(any(), any(), any())).thenReturn(true)
            whenever(queryExecutor.execute(any(), any(), any(), any())).thenReturn("1\tAlice")
            whenever(resultComparator.compare(any(), any(), any())).thenReturn(true)

            val result = gradingService.grade(submissionId)

            assertThat(result).isEqualTo(SubmissionVerdict.ACCEPTED)
        }

        @Test
        fun `should return WRONG_ANSWER when result mismatch`() {
            val submission = createSubmission()
            val problem = createProblem()
            val testCases = listOf(createTestCase(1L))

            whenever(submissionRepository.findById(submissionId)).thenReturn(submission)
            whenever(problemRepository.findById(problemId)).thenReturn(problem)
            whenever(testCaseRepository.findAllByProblemId(problemId)).thenReturn(testCases)
            whenever(submissionRepository.updateStatus(any(), any(), any())).thenReturn(true)
            whenever(queryExecutor.execute(any(), any(), any(), any())).thenReturn("wrong")
            whenever(resultComparator.compare(any(), any(), any())).thenReturn(false)

            val result = gradingService.grade(submissionId)

            assertThat(result).isEqualTo(SubmissionVerdict.WRONG_ANSWER)
        }

        @Test
        fun `should return TIME_LIMIT_EXCEEDED when query timeout`() {
            val submission = createSubmission()
            val problem = createProblem()
            val testCases = listOf(createTestCase(1L))

            whenever(submissionRepository.findById(submissionId)).thenReturn(submission)
            whenever(problemRepository.findById(problemId)).thenReturn(problem)
            whenever(testCaseRepository.findAllByProblemId(problemId)).thenReturn(testCases)
            whenever(submissionRepository.updateStatus(any(), any(), any())).thenReturn(true)
            whenever(queryExecutor.execute(any(), any(), any(), any()))
                .thenThrow(QueryTimeoutException("timeout"))

            val result = gradingService.grade(submissionId)

            assertThat(result).isEqualTo(SubmissionVerdict.TIME_LIMIT_EXCEEDED)
        }

        @Test
        fun `should return RUNTIME_ERROR when query execution fails`() {
            val submission = createSubmission()
            val problem = createProblem()
            val testCases = listOf(createTestCase(1L))

            whenever(submissionRepository.findById(submissionId)).thenReturn(submission)
            whenever(problemRepository.findById(problemId)).thenReturn(problem)
            whenever(testCaseRepository.findAllByProblemId(problemId)).thenReturn(testCases)
            whenever(submissionRepository.updateStatus(any(), any(), any())).thenReturn(true)
            whenever(queryExecutor.execute(any(), any(), any(), any()))
                .thenThrow(QueryExecutionException("SQL error"))

            val result = gradingService.grade(submissionId)

            assertThat(result).isEqualTo(SubmissionVerdict.RUNTIME_ERROR)
        }

        @Test
        fun `should update status to RUNNING then COMPLETED`() {
            val submission = createSubmission()
            val problem = createProblem()
            val testCases = listOf(createTestCase(1L))

            whenever(submissionRepository.findById(submissionId)).thenReturn(submission)
            whenever(problemRepository.findById(problemId)).thenReturn(problem)
            whenever(testCaseRepository.findAllByProblemId(problemId)).thenReturn(testCases)
            whenever(submissionRepository.updateStatus(any(), any(), any())).thenReturn(true)
            whenever(queryExecutor.execute(any(), any(), any(), any())).thenReturn("1\tAlice")
            whenever(resultComparator.compare(any(), any(), any())).thenReturn(true)

            gradingService.grade(submissionId)

            val inOrder: InOrder = inOrder(submissionRepository)
            inOrder.verify(submissionRepository).updateStatus(eq(submissionId), eq(SubmissionStatus.RUNNING), eq(null))
            inOrder.verify(submissionRepository).updateStatus(eq(submissionId), eq(SubmissionStatus.COMPLETED), eq(SubmissionVerdict.ACCEPTED))
        }

        @Test
        fun `should send SSE events for status changes`() {
            val submission = createSubmission()
            val problem = createProblem()
            val testCases = listOf(createTestCase(1L))

            whenever(submissionRepository.findById(submissionId)).thenReturn(submission)
            whenever(problemRepository.findById(problemId)).thenReturn(problem)
            whenever(testCaseRepository.findAllByProblemId(problemId)).thenReturn(testCases)
            whenever(submissionRepository.updateStatus(any(), any(), any())).thenReturn(true)
            whenever(queryExecutor.execute(any(), any(), any(), any())).thenReturn("1\tAlice")
            whenever(resultComparator.compare(any(), any(), any())).thenReturn(true)

            gradingService.grade(submissionId)

            val inOrder: InOrder = inOrder(sseEmitterService)
            inOrder.verify(sseEmitterService).send(eq(submissionId), eq(SubmissionStatus.RUNNING), eq(null))
            inOrder.verify(sseEmitterService).send(eq(submissionId), eq(SubmissionStatus.COMPLETED), eq(SubmissionVerdict.ACCEPTED))
        }
    }

    @Nested
    inner class Exceptions {

        @Test
        fun `should throw SUBMISSION_NOT_FOUND when submission does not exist`() {
            whenever(submissionRepository.findById(submissionId)).thenReturn(null)

            assertThatThrownBy { gradingService.grade(submissionId) }
                .isInstanceOf(BusinessException::class.java)
                .extracting("errorCode")
                .isEqualTo(SubmissionErrorCode.SUBMISSION_NOT_FOUND)
        }

        @Test
        fun `should throw PROBLEM_NOT_FOUND when problem does not exist`() {
            val submission = createSubmission()

            whenever(submissionRepository.findById(submissionId)).thenReturn(submission)
            whenever(problemRepository.findById(problemId)).thenReturn(null)

            assertThatThrownBy { gradingService.grade(submissionId) }
                .isInstanceOf(BusinessException::class.java)
                .extracting("errorCode")
                .isEqualTo(ProblemErrorCode.PROBLEM_NOT_FOUND)
        }
    }
}
