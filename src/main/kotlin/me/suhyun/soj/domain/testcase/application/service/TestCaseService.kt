package me.suhyun.soj.domain.testcase.application.service

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.domain.testcase.exception.TestCaseErrorCode
import me.suhyun.soj.domain.testcase.presentation.request.CreateTestCaseRequest
import me.suhyun.soj.domain.testcase.presentation.request.UpdateTestCaseRequest
import me.suhyun.soj.domain.testcase.presentation.response.TestCaseResponse
import me.suhyun.soj.global.common.util.SqlGenerator
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class TestCaseService(
    private val testCaseRepository: TestCaseRepository,
    private val problemRepository: ProblemRepository
) {

    // TODO 보일 예시와 테스트 케이스 구분
    @CacheEvict(value = ["testcases"], key = "#problemId")
    fun create(problemId: Long, request: CreateTestCaseRequest) {
        val problem = problemRepository.findById(problemId)
            ?: throw BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)

        val initSql = request.initData?.let { SqlGenerator.generateInit(it) }
        val answer = SqlGenerator.generateAnswer(request.answerData)
        testCaseRepository.save(
            TestCase(
                id = null,
                problemId = problem.id!!,
                initSql = initSql,
                initMetadata = request.initData,
                answer = answer,
                answerMetadata = request.answerData,
                isVisible = request.isVisible,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["testcases"], key = "#problemId + '_' + #isVisible")
    fun findAll(problemId: Long, isVisible: Boolean? = true): List<TestCaseResponse> {
        return testCaseRepository.findAllByProblemId(problemId, isVisible)
            .map { TestCaseResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun findById(problemId: Long, testcaseId: Long): TestCaseResponse {
        val testCase = testCaseRepository.findById(testcaseId)
            ?: throw BusinessException(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
        if (testCase.problemId != problemId) {
            throw BusinessException(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
        }
        return TestCaseResponse.from(testCase)
    }

    @CacheEvict(value = ["testcases"], key = "#problemId")
    fun update(problemId: Long, testcaseId: Long, request: UpdateTestCaseRequest) {
        val testCase = testCaseRepository.findById(testcaseId)
            ?: throw BusinessException(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
        if (testCase.problemId != problemId) {
            throw BusinessException(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
        }
        val initSql = request.initData?.let { SqlGenerator.generateInit(it) }
        val answer = request.answerData?.let { SqlGenerator.generateAnswer(it) }
        testCaseRepository.update(testcaseId, initSql, request.initData, answer, request.answerData, request.isVisible)
    }

    @CacheEvict(value = ["testcases"], key = "#problemId")
    fun delete(problemId: Long, testcaseId: Long) {
        val testCase = testCaseRepository.findById(testcaseId)
            ?: throw BusinessException(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
        if (testCase.problemId != problemId) {
            throw BusinessException(TestCaseErrorCode.TEST_CASE_NOT_FOUND)
        }
        testCaseRepository.softDelete(testcaseId)
    }
}
