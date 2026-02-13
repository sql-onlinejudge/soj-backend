package me.suhyun.soj.domain.workbook.presentation

import jakarta.validation.Valid
import me.suhyun.soj.domain.problem.presentation.response.ProblemResponse
import me.suhyun.soj.domain.workbook.application.service.WorkbookProblemService
import me.suhyun.soj.domain.workbook.presentation.request.CreateWorkbookProblemRequest
import me.suhyun.soj.global.common.dto.PageResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workbooks/{workbookId}/problems")
class WorkbookProblemController(
    private val workbookProblemService: WorkbookProblemService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable workbookId: Long,
        @Valid @RequestBody request: CreateWorkbookProblemRequest
    ) {
        workbookProblemService.create(workbookId, request)
    }

    @GetMapping
    fun findAll(
        @PathVariable workbookId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): PageResponse<ProblemResponse> {
        return workbookProblemService.findAll(
            workbookId = workbookId,
            page = page,
            size = size,
        )
    }

    @DeleteMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable workbookId: Long,
        @PathVariable problemId: Long
    ) {
        workbookProblemService.delete(workbookId, problemId)
    }
}
