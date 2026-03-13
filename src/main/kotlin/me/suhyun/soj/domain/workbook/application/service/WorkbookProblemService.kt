package me.suhyun.soj.domain.workbook.application.service

import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.presentation.response.ProblemResponse
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.workbook.domain.model.WorkbookProblem
import me.suhyun.soj.domain.workbook.domain.repository.WorkbookProblemRepository
import me.suhyun.soj.domain.workbook.exception.WorkbookErrorCode
import me.suhyun.soj.domain.workbook.presentation.request.CreateWorkbookProblemRequest
import me.suhyun.soj.global.common.dto.PageResponse
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class WorkbookProblemService(
    private val workbookProblemRepository: WorkbookProblemRepository,
    private val problemRepository: ProblemRepository,
    private val submissionRepository: SubmissionRepository,
) {
    fun create(workbookId: Long, request: CreateWorkbookProblemRequest) {
        val existing = workbookProblemRepository.findByWorkbookIdAndProblemId(workbookId, request.problemId)
        if (existing != null) {
            throw BusinessException(WorkbookErrorCode.WORKBOOK_PROBLEM_ALREADY_EXISTS)
        }
        workbookProblemRepository.save(
            WorkbookProblem(
                id = null,
                workbookId = workbookId,
                problemId = request.problemId
            )
        )
    }

    @Transactional(readOnly = true)
    fun findAll(
        workbookId: Long,
        page: Int,
        size: Int,
    ): PageResponse<ProblemResponse> {
        val userId = SecurityContextHolder.getContext().authentication?.principal as? UUID
        val workbookProblems = workbookProblemRepository.findAllByWorkbookId(workbookId, page, size)
        val totalElements = workbookProblemRepository.countByWorkbookId(workbookId)

        val problems = workbookProblems.mapNotNull { wp ->
            problemRepository.findById(wp.problemId)
        }

        val problemIds = problems.mapNotNull { it.id }
        val trialStatuses = if (userId != null) getTrialStatuses(problemIds, userId) else emptyMap()

        return PageResponse.of(
            content = problems.map { ProblemResponse.from(it, trialStatuses[it.id]) },
            page = page,
            size = size,
            totalElements = totalElements,
        )
    }

    private fun getTrialStatuses(problemIds: List<Long>, userId: UUID): Map<Long, TrialStatus> {
        if (problemIds.isEmpty()) return emptyMap()
        val statuses = submissionRepository.getTrialStatuses(problemIds, userId)
        return problemIds.associateWith { problemId ->
            when {
                problemId !in statuses -> TrialStatus.NOT_ATTEMPTED
                statuses[problemId] == true -> TrialStatus.SOLVED
                else -> TrialStatus.ATTEMPTED
            }
        }
    }

    fun delete(workbookId: Long, problemId: Long) {
        val workbookProblem = workbookProblemRepository
            .findByWorkbookIdAndProblemId(workbookId, problemId)
            ?: throw BusinessException(WorkbookErrorCode.WORKBOOK_PROBLEM_NOT_FOUND)
        val deleted = workbookProblemRepository.delete(workbookProblem.id!!)
        if (!deleted) {
            throw BusinessException(WorkbookErrorCode.WORKBOOK_PROBLEM_NOT_FOUND)
        }
    }
}
