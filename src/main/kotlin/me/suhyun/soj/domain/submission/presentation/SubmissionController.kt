package me.suhyun.soj.domain.submission.presentation

import jakarta.validation.Valid
import me.suhyun.soj.domain.submission.application.service.SubmissionService
import me.suhyun.soj.domain.submission.presentation.request.SubmitRequest
import me.suhyun.soj.domain.submission.presentation.response.SubmissionDetailResponse
import me.suhyun.soj.domain.submission.presentation.response.SubmissionResponse
import me.suhyun.soj.domain.submission.presentation.response.SubmitResponse
import me.suhyun.soj.global.common.dto.PageResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/problems/{problemId}/submissions")
class SubmissionController(
    private val submissionService: SubmissionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun submit(
        @PathVariable problemId: Long,
        @Valid @RequestBody request: SubmitRequest
    ): SubmitResponse {
        return SubmitResponse(submissionService.submit(problemId, request))
    }

    @GetMapping
    fun findAll(
        @PathVariable problemId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<SubmissionResponse> {
        return submissionService.findByProblemId(problemId, page, size)
    }

    @GetMapping("/{submissionId}")
    fun findById(
        @PathVariable problemId: Long,
        @PathVariable submissionId: Long
    ): SubmissionDetailResponse {
        return submissionService.findById(problemId, submissionId)
    }
}
