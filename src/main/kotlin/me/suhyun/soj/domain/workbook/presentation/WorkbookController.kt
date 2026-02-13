package me.suhyun.soj.domain.workbook.presentation

import jakarta.validation.Valid
import me.suhyun.soj.domain.workbook.application.service.WorkbookService
import me.suhyun.soj.domain.workbook.presentation.request.CreateWorkbookRequest
import me.suhyun.soj.domain.workbook.presentation.request.UpdateWorkbookRequest
import me.suhyun.soj.domain.workbook.presentation.response.WorkbookResponse
import me.suhyun.soj.global.common.dto.PageResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workbooks")
class WorkbookController(
    private val workbookService: WorkbookService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateWorkbookRequest) {
        workbookService.create(request)
    }

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) minDifficulty: Int?,
        @RequestParam(required = false) maxDifficulty: Int?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "id:desc") sort: List<String>,
    ): PageResponse<WorkbookResponse> {
        return workbookService.findAll(
            page = page,
            size = size,
            minDifficulty = minDifficulty,
            maxDifficulty = maxDifficulty,
            keyword = keyword,
            sort = sort,
        )
    }

    @GetMapping("/{workbookId}")
    fun findById(@PathVariable workbookId: Long): WorkbookResponse {
        return workbookService.findById(workbookId)
    }

    @PatchMapping("/{workbookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @PathVariable workbookId: Long,
        @Valid @RequestBody request: UpdateWorkbookRequest
    ) {
        workbookService.update(workbookId, request)
    }

    @DeleteMapping("/{workbookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable workbookId: Long) {
        workbookService.delete(workbookId)
    }
}
