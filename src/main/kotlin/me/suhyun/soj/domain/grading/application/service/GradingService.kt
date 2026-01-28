package me.suhyun.soj.domain.grading.application.service

import me.suhyun.soj.domain.grading.exception.QueryExecutionException
import me.suhyun.soj.domain.grading.exception.QueryTimeoutException
import me.suhyun.soj.domain.grading.infrastructure.QueryExecutor
import me.suhyun.soj.domain.grading.infrastructure.sse.SseEmitterService
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.submission.exception.SubmissionErrorCode
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GradingService(
    private val submissionRepository: SubmissionRepository,
    private val problemRepository: ProblemRepository,
    private val testCaseRepository: TestCaseRepository,
    private val queryExecutor: QueryExecutor,
    private val resultComparator: ResultComparator,
    private val sseEmitterService: SseEmitterService
) {

    fun grade(submissionId: Long): SubmissionVerdict {
        val submission = submissionRepository.findById(submissionId)
            ?: throw BusinessException(SubmissionErrorCode.SUBMISSION_NOT_FOUND)

        val problem = problemRepository.findById(submission.problemId)
            ?: throw BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)

        val testCases = testCaseRepository.findAllByProblemId(problem.id!!)

        submissionRepository.updateStatus(submissionId, SubmissionStatus.RUNNING, null)
        sseEmitterService.send(submissionId, SubmissionStatus.RUNNING, null)

        val verdict = try {
            var allPassed = true

            for (testCase in testCases) {
                val actualResult = queryExecutor.execute(
                    schemaSql = problem.schemaSql,
                    initSql = testCase.initSql,
                    query = submission.query,
                    timeoutMs = problem.timeLimit
                )

                val isMatch = resultComparator.compare(
                    actual = actualResult,
                    expected = testCase.answer,
                    isOrderSensitive = problem.isOrderSensitive
                )

                if (!isMatch) {
                    allPassed = false
                    break
                }
            }

            if (allPassed) SubmissionVerdict.ACCEPTED else SubmissionVerdict.WRONG_ANSWER
        } catch (e: QueryTimeoutException) {
            SubmissionVerdict.TIME_LIMIT_EXCEEDED
        } catch (e: QueryExecutionException) {
            SubmissionVerdict.RUNTIME_ERROR
        }

        submissionRepository.updateStatus(submissionId, SubmissionStatus.COMPLETED, verdict)
        sseEmitterService.send(submissionId, SubmissionStatus.COMPLETED, verdict)

        if (verdict == SubmissionVerdict.ACCEPTED) {
            problemRepository.incrementSolvedCount(submission.problemId)
        }

        return verdict
    }
}
