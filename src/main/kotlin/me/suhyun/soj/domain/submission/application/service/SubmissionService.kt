package me.suhyun.soj.domain.submission.application.service

import me.suhyun.soj.domain.grading.application.event.SubmissionCreatedEvent
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.Submission
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.submission.exception.SubmissionErrorCode
import me.suhyun.soj.domain.submission.presentation.request.SubmitRequest
import me.suhyun.soj.domain.submission.presentation.response.SubmissionDetailResponse
import me.suhyun.soj.domain.submission.presentation.response.SubmissionResponse
import me.suhyun.soj.global.common.dto.PageResponse
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class SubmissionService(
    private val submissionRepository: SubmissionRepository,
    private val problemRepository: ProblemRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val redisTemplate: StringRedisTemplate
) {

    fun submit(problemId: Long, request: SubmitRequest): Long {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID
        val saved = submissionRepository.save(
            Submission(
                id = null,
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

        redisTemplate.opsForValue().increment("problem:submissionCount:$problemId")
        eventPublisher.publishEvent(SubmissionCreatedEvent(saved.id!!, saved.query))

        return saved.id
    }

    @Transactional(readOnly = true)
    fun findByProblemId(problemId: Long, page: Int, size: Int): PageResponse<SubmissionResponse> {
        val userId = SecurityContextHolder.getContext().authentication?.principal as? UUID
        return if (userId != null) {
            val submissions = submissionRepository.findByProblemIdAndUserId(problemId, userId, page, size)
            val totalElements = submissionRepository.countByProblemIdAndUserId(problemId, userId)
            PageResponse.of(content = submissions.map { SubmissionResponse.from(it) }, page = page, size = size, totalElements = totalElements)
        } else {
            val submissions = submissionRepository.findByProblemId(problemId, page, size)
            val totalElements = submissionRepository.countByProblemId(problemId)
            PageResponse.of(content = submissions.map { SubmissionResponse.from(it) }, page = page, size = size, totalElements = totalElements)
        }
    }

    @Transactional(readOnly = true)
    fun findById(problemId: Long, submissionId: Long): SubmissionDetailResponse {
        val submission = submissionRepository.findById(submissionId)
            ?: throw BusinessException(SubmissionErrorCode.SUBMISSION_NOT_FOUND)
        if (submission.problemId != problemId) {
            throw BusinessException(SubmissionErrorCode.SUBMISSION_NOT_FOUND)
        }
        return SubmissionDetailResponse.from(submission)
    }
}
