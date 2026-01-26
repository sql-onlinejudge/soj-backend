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
class SubmissionServiceFindAllByProblemIdTest {

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    private lateinit var submissionService: SubmissionService

    @BeforeEach
    fun setUp() {
        submissionService = SubmissionService(submissionRepository)
    }

    private fun createSubmission(
        id: Long,
        problemId: Long,
        userId: UUID = UUID.randomUUID(),
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
    fun `should return paginated submissions by problemId`() {
        val problemId = 1L
        val submissions = listOf(
            createSubmission(1L, problemId),
            createSubmission(2L, problemId),
            createSubmission(3L, problemId)
        )

        whenever(submissionRepository.findByProblemId(problemId, 0, 10)).thenReturn(submissions)
        whenever(submissionRepository.countByProblemId(problemId)).thenReturn(3L)

        val result = submissionService.findAllByProblemId(problemId, 0, 10)

        assertThat(result.content).hasSize(3)
        assertThat(result.totalElements).isEqualTo(3L)
        assertThat(result.page).isEqualTo(0)
        assertThat(result.size).isEqualTo(10)
    }

    @Test
    fun `should return empty content when no submissions exist`() {
        val problemId = 1L

        whenever(submissionRepository.findByProblemId(problemId, 0, 10)).thenReturn(emptyList())
        whenever(submissionRepository.countByProblemId(problemId)).thenReturn(0L)

        val result = submissionService.findAllByProblemId(problemId, 0, 10)

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0L)
    }

    @Test
    fun `should return correct pagination info`() {
        val problemId = 1L
        val submissions = listOf(createSubmission(1L, problemId))

        whenever(submissionRepository.findByProblemId(problemId, 2, 5)).thenReturn(submissions)
        whenever(submissionRepository.countByProblemId(problemId)).thenReturn(15L)

        val result = submissionService.findAllByProblemId(problemId, 2, 5)

        assertThat(result.page).isEqualTo(2)
        assertThat(result.size).isEqualTo(5)
        assertThat(result.totalElements).isEqualTo(15L)
    }
}
