package me.suhyun.soj.domain.submission.application.service

import me.suhyun.soj.domain.submission.domain.model.Submission
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.submission.exception.SubmissionErrorCode
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
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SubmissionServiceFindByIdTest {

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    private lateinit var submissionService: SubmissionService

    @BeforeEach
    fun setUp() {
        submissionService = SubmissionService(submissionRepository)
    }

    @Test
    fun `should return submission by id successfully`() {
        val problemId = 1L
        val submissionId = 1L
        val userId = UUID.randomUUID()
        val submission = Submission(
            id = submissionId,
            problemId = problemId,
            userId = userId,
            query = "SELECT * FROM users",
            status = SubmissionStatus.COMPLETED,
            verdict = SubmissionVerdict.ACCEPTED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )

        whenever(submissionRepository.findById(submissionId)).thenReturn(submission)

        val result = submissionService.findById(problemId, submissionId)

        assertThat(result.id).isEqualTo(submissionId)
        assertThat(result.problemId).isEqualTo(problemId)
        assertThat(result.status).isEqualTo(SubmissionStatus.COMPLETED)
        assertThat(result.verdict).isEqualTo(SubmissionVerdict.ACCEPTED)
    }

    @Test
    fun `should throw SUBMISSION_NOT_FOUND when submission does not exist`() {
        val problemId = 1L
        val submissionId = 999L

        whenever(submissionRepository.findById(submissionId)).thenReturn(null)

        assertThatThrownBy { submissionService.findById(problemId, submissionId) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(SubmissionErrorCode.SUBMISSION_NOT_FOUND)
    }

    @Test
    fun `should throw SUBMISSION_NOT_FOUND when problemId mismatch`() {
        val problemId = 1L
        val differentProblemId = 2L
        val submissionId = 1L
        val userId = UUID.randomUUID()
        val submission = Submission(
            id = submissionId,
            problemId = differentProblemId,
            userId = userId,
            query = "SELECT * FROM users",
            status = SubmissionStatus.COMPLETED,
            verdict = SubmissionVerdict.ACCEPTED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )

        whenever(submissionRepository.findById(submissionId)).thenReturn(submission)

        assertThatThrownBy { submissionService.findById(problemId, submissionId) }
            .isInstanceOf(BusinessException::class.java)
            .extracting("errorCode")
            .isEqualTo(SubmissionErrorCode.SUBMISSION_NOT_FOUND)
    }
}
