package me.suhyun.soj.domain.submission.application.service

import me.suhyun.soj.domain.submission.domain.model.Submission
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.submission.presentation.request.SubmitRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SubmissionServiceSubmitTest {

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    @Captor
    private lateinit var submissionCaptor: ArgumentCaptor<Submission>

    private lateinit var submissionService: SubmissionService

    @BeforeEach
    fun setUp() {
        submissionService = SubmissionService(submissionRepository)
    }

    @Test
    fun `should create submission with PENDING status`() {
        val problemId = 1L
        val userId = UUID.randomUUID()
        val request = SubmitRequest(query = "SELECT * FROM users")

        whenever(submissionRepository.save(any())).thenReturn(
            Submission(
                id = 1L,
                problemId = problemId,
                userId = userId,
                query = request.query,
                status = SubmissionStatus.PENDING,
                verdict = null,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )

        submissionService.submit(problemId, userId, request)

        verify(submissionRepository).save(capture(submissionCaptor))
        val captured = submissionCaptor.value
        assertThat(captured.status).isEqualTo(SubmissionStatus.PENDING)
        assertThat(captured.verdict).isNull()
    }

    @Test
    fun `should save submission with correct problemId and userId`() {
        val problemId = 1L
        val userId = UUID.randomUUID()
        val request = SubmitRequest(query = "SELECT id FROM products WHERE price > 100")

        whenever(submissionRepository.save(any())).thenReturn(
            Submission(
                id = 1L,
                problemId = problemId,
                userId = userId,
                query = request.query,
                status = SubmissionStatus.PENDING,
                verdict = null,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )

        submissionService.submit(problemId, userId, request)

        verify(submissionRepository).save(capture(submissionCaptor))
        val captured = submissionCaptor.value
        assertThat(captured.problemId).isEqualTo(problemId)
        assertThat(captured.userId).isEqualTo(userId)
        assertThat(captured.query).isEqualTo("SELECT id FROM products WHERE price > 100")
    }
}
