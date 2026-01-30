package me.suhyun.soj.domain.problem.presentation

import jakarta.validation.Valid
import me.suhyun.soj.domain.problem.application.service.ProblemService
import me.suhyun.soj.domain.problem.presentation.request.CreateProblemRequest
import me.suhyun.soj.domain.problem.presentation.request.UpdateProblemRequest
import me.suhyun.soj.domain.problem.presentation.response.ProblemDetailResponse
import me.suhyun.soj.domain.problem.presentation.response.ProblemResponse
import me.suhyun.soj.global.common.dto.PageResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/problems")
class ProblemController(
    private val problemService: ProblemService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateProblemRequest
    ) {
        problemService.create(request)
    }

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) minDifficulty: Int?,
        @RequestParam(required = false) maxDifficulty: Int?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "id:desc") sort: List<String>,
        @RequestHeader("X-User-Id") userId: String,
    ): PageResponse<ProblemResponse> {
        return problemService.findAll(
            page = page,
            size = size,
            minDifficulty = minDifficulty,
            maxDifficulty = maxDifficulty,
            keyword = keyword,
            sort = sort,
            userId = UUID.fromString(userId)
        )
    }

    @GetMapping("/{problemId}")
    fun findById(
        @PathVariable problemId: Long,
        @RequestHeader(value = "X-User-Id", required = false) userId: String?,
    ): ProblemDetailResponse {
        return problemService.findById(problemId, userId?.let { UUID.fromString(it) })
    }

    @PatchMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @PathVariable problemId: Long,
        @Valid @RequestBody request: UpdateProblemRequest
    ) {
        problemService.update(problemId, request)
    }

    @DeleteMapping("/{problemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable problemId: Long) {
        problemService.delete(problemId)
    }
}
