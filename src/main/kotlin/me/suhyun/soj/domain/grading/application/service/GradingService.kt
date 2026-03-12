package me.suhyun.soj.domain.grading.application.service

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import me.suhyun.soj.domain.grading.exception.QueryExecutionException
import me.suhyun.soj.domain.grading.exception.QueryTimeoutException
import me.suhyun.soj.domain.grading.infrastructure.QueryExecutor
import me.suhyun.soj.domain.grading.infrastructure.QueryValidator
import me.suhyun.soj.domain.grading.infrastructure.sse.SseEmitterService
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.submission.exception.SubmissionErrorCode
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.global.exception.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GradingService(
    private val submissionRepository: SubmissionRepository,
    private val problemRepository: ProblemRepository,
    private val testCaseRepository: TestCaseRepository,
    private val queryExecutor: QueryExecutor,
    private val queryValidator: QueryValidator,
    private val resultComparator: ResultComparator,
    private val sseEmitterService: SseEmitterService,
    private val meterRegistry: MeterRegistry
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val gradingTimer = Timer.builder("grading.request.duration")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry)

    private val errorCounter = Counter.builder("grading.errors")
        .register(meterRegistry)

    fun grade(submissionId: Long): SubmissionVerdict {
        return gradingTimer.recordCallable { doGrade(submissionId) }!!
    }

    private fun doGrade(submissionId: Long): SubmissionVerdict {
        val submission = submissionRepository.findById(submissionId)
            ?: throw BusinessException(SubmissionErrorCode.SUBMISSION_NOT_FOUND)

        val problem = problemRepository.findById(submission.problemId)
            ?: throw BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)

        val testCases = testCaseRepository.findAllByProblemId(problem.id!!, null)

        submissionRepository.updateStatus(submissionId, SubmissionStatus.RUNNING, null)
        sseEmitterService.send(submissionId, SubmissionStatus.RUNNING, null)

        val verdict = try {
            queryValidator.validate(submission.query)

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

                log.info("grading start submissionId={}, query={}", submissionId, submission.query)
                log.info("user result rows={}", actualResult)
                log.info("answer result rows={}", testCase.answer)

                if (!isMatch) {
                    allPassed = false
                    break
                }
            }

            if (allPassed) SubmissionVerdict.ACCEPTED else SubmissionVerdict.WRONG_ANSWER
        } catch (e: BusinessException) {
            errorCounter.increment()
            log.error("grading failed: business exception, submissionId={}", submissionId, e)
            SubmissionVerdict.INVALID_QUERY
        } catch (e: QueryTimeoutException) {
            errorCounter.increment()
            log.error("grading failed: timeout, submissionId={}", submissionId, e)
            SubmissionVerdict.TIME_LIMIT_EXCEEDED
        } catch (e: QueryExecutionException) {
            errorCounter.increment()
            log.error("grading failed: execution error, submissionId={}", submissionId, e)
            SubmissionVerdict.RUNTIME_ERROR
        }

        meterRegistry.counter("grading.verdict", "result", verdict.name).increment()

        submissionRepository.updateStatus(submissionId, SubmissionStatus.COMPLETED, verdict)
        sseEmitterService.send(submissionId, SubmissionStatus.COMPLETED, verdict)

        if (verdict == SubmissionVerdict.ACCEPTED) {
            problemRepository.incrementSolvedCount(submission.problemId)
        }

        return verdict
    }
}
