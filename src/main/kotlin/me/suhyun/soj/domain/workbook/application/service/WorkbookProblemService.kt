package me.suhyun.soj.domain.workbook.application.service

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.presentation.response.ProblemResponse
import me.suhyun.soj.domain.workbook.domain.model.WorkbookProblem
import me.suhyun.soj.domain.workbook.domain.repository.WorkbookProblemRepository
import me.suhyun.soj.domain.workbook.exception.WorkbookErrorCode
import me.suhyun.soj.domain.workbook.presentation.request.CreateWorkbookProblemRequest
import me.suhyun.soj.global.common.dto.PageResponse
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkbookProblemService(
    private val workbookProblemRepository: WorkbookProblemRepository,
    private val problemRepository: ProblemRepository,
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
        val workbookProblems = workbookProblemRepository.findAllByWorkbookId(workbookId, page, size)
        val totalElements = workbookProblemRepository.countByWorkbookId(workbookId)

        val problems = workbookProblems.mapNotNull { wp ->
            problemRepository.findById(wp.problemId)?.let { ProblemResponse.from(it) }
        }

        return PageResponse.of(
            content = problems,
            page = page,
            size = size,
            totalElements = totalElements,
        )
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
