package me.suhyun.soj.domain.testcase.presentation

import jakarta.validation.Valid
import me.suhyun.soj.domain.testcase.application.service.TestCaseService
import me.suhyun.soj.domain.testcase.presentation.request.CreateTestCaseRequest
import me.suhyun.soj.domain.testcase.presentation.request.UpdateTestCaseRequest
import me.suhyun.soj.domain.testcase.presentation.response.TestCaseResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/problems/{problemId}/testcases")
class TestCaseController(
    private val testCaseService: TestCaseService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable problemId: Long,
        @Valid @RequestBody request: CreateTestCaseRequest
    ) {
        testCaseService.create(problemId, request)
    }

    @GetMapping
    fun findAll(
        @PathVariable problemId: Long,
        @RequestParam(defaultValue = "true") isVisible: Boolean?
    ): List<TestCaseResponse> {
        return testCaseService.findAll(problemId, isVisible)
    }

    @GetMapping("/{testcaseId}")
    fun findById(
        @PathVariable problemId: Long,
        @PathVariable testcaseId: Long
    ): TestCaseResponse {
        return testCaseService.findById(problemId, testcaseId)
    }

    @PatchMapping("/{testcaseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateTestCase(
        @PathVariable problemId: Long,
        @PathVariable testcaseId: Long,
        @Valid @RequestBody request: UpdateTestCaseRequest
    ) {
        testCaseService.update(problemId, testcaseId, request)
    }

    @DeleteMapping("/{testcaseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTestCase(
        @PathVariable problemId: Long,
        @PathVariable testcaseId: Long
    ) {
        testCaseService.delete(problemId, testcaseId)
    }
}
