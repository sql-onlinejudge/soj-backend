package me.suhyun.soj.domain.submission.application.service

import me.suhyun.soj.domain.submission.domain.model.Submission
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
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
class SubmissionServiceFindMyByProblemIdTest {

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    private lateinit var submissionService: SubmissionService

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        submissionService = SubmissionService(submissionRepository)
    }

    private fun createSubmission(
        id: Long,
        problemId: Long,
        status: SubmissionStatus = SubmissionStatus.COMPLETED,
        verdict: SubmissionVerdict? = SubmissionVerdict.ACCEPTED
    ): Submission {
        return Submission(
            id = id,
            problemId = problemId,
            userId = userId,
            query = "SELECT * FROM test",
            status = status,
            verdict = verdict,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }

    @Test
    fun `should return paginated submissions by problemId and userId`() {
        val problemId = 1L
        val submissions = listOf(
            createSubmission(1L, problemId),
            createSubmission(2L, problemId)
        )

        whenever(submissionRepository.findByProblemIdAndUserId(problemId, userId, 0, 10)).thenReturn(submissions)
        whenever(submissionRepository.countByProblemIdAndUserId(problemId, userId)).thenReturn(2L)

        val result = submissionService.findMyByProblemId(problemId, userId, 0, 10)

        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2L)
    }

    @Test
    fun `should return empty content when user has no submissions`() {
        val problemId = 1L

        whenever(submissionRepository.findByProblemIdAndUserId(problemId, userId, 0, 10)).thenReturn(emptyList())
        whenever(submissionRepository.countByProblemIdAndUserId(problemId, userId)).thenReturn(0L)

        val result = submissionService.findMyByProblemId(problemId, userId, 0, 10)

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0L)
    }

    @Test
    fun `should return correct pagination info for user submissions`() {
        val problemId = 1L
        val submissions = listOf(createSubmission(5L, problemId))

        whenever(submissionRepository.findByProblemIdAndUserId(problemId, userId, 1, 3)).thenReturn(submissions)
        whenever(submissionRepository.countByProblemIdAndUserId(problemId, userId)).thenReturn(7L)

        val result = submissionService.findMyByProblemId(problemId, userId, 1, 3)

        assertThat(result.page).isEqualTo(1)
        assertThat(result.size).isEqualTo(3)
        assertThat(result.totalElements).isEqualTo(7L)
    }
}
