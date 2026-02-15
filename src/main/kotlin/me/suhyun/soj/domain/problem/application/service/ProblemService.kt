package me.suhyun.soj.domain.problem.application.service

import me.suhyun.soj.domain.problem.application.event.ProblemEvent
import me.suhyun.soj.domain.problem.application.event.ProblemEventPublisher
import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.problem.presentation.request.CreateProblemRequest
import me.suhyun.soj.domain.problem.presentation.request.UpdateProblemRequest
import me.suhyun.soj.domain.problem.presentation.response.ProblemDetailResponse
import me.suhyun.soj.domain.problem.presentation.response.ProblemResponse
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import me.suhyun.soj.global.common.dto.PageResponse
import me.suhyun.soj.global.common.util.SqlGenerator
import me.suhyun.soj.global.exception.BusinessException
import me.suhyun.soj.global.infrastructure.cache.CacheKeys
import me.suhyun.soj.global.infrastructure.cache.CacheService
import me.suhyun.soj.global.infrastructure.cache.config.CacheProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val testCaseRepository: TestCaseRepository,
    private val submissionRepository: SubmissionRepository,
    private val cacheService: CacheService,
    private val cacheProperties: CacheProperties,
    private val problemEventPublisher: ProblemEventPublisher,
    private val problemSearchService: me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemSearchService
) {

    fun create(request: CreateProblemRequest) {
        val schemaSql = SqlGenerator.generateSchema(request.schemaMetadata)
        val savedProblem = problemRepository.save(
            Problem(
                id = null,
                title = request.title,
                description = request.description,
                schemaSql = schemaSql,
                schemaMetadata = request.schemaMetadata,
                difficulty = request.difficulty,
                timeLimit = request.timeLimit,
                isOrderSensitive = request.isOrderSensitive,
                solvedCount = 0,
                submissionCount = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                deletedAt = null
            )
        )

        request.testcases.forEach { input ->
            val initSql = input.initData?.let { SqlGenerator.generateInit(it) }
            val answer = SqlGenerator.generateAnswer(input.answerData)
            testCaseRepository.save(
                TestCase(
                    id = null,
                    problemId = savedProblem.id!!,
                    initSql = initSql,
                    initMetadata = input.initData,
                    answer = answer,
                    answerMetadata = input.answerData,
                    createdAt = LocalDateTime.now(),
                    updatedAt = null,
                    deletedAt = null
                )
            )
        }

        problemEventPublisher.publish(ProblemEvent.Created(savedProblem.id!!))
    }

    @Transactional(readOnly = true)
    fun findAll(
        page: Int,
        size: Int,
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        sort: List<String>,
        userId: UUID,
        trialStatus: TrialStatus? = null
    ): PageResponse<ProblemResponse> {
        val problems: List<Problem>
        val totalElements: Long

        if (keyword != null) {
            val allProblemIds = problemSearchService.search(keyword)
            val filteredProblems = problemRepository.findByIdsWithFilters(
                ids = allProblemIds,
                minDifficulty = minDifficulty,
                maxDifficulty = maxDifficulty,
                trialStatus = trialStatus,
                userId = userId,
                sort = sort
            )
            totalElements = filteredProblems.size.toLong()
            problems = filteredProblems.drop(page * size).take(size)
        } else {
            problems = problemRepository.findAll(
                page = page,
                size = size,
                minDifficulty = minDifficulty,
                maxDifficulty = maxDifficulty,
                keyword = null,
                sort = sort,
                trialStatus = trialStatus,
                userId = userId
            )
            totalElements = problemRepository.countAll(
                minDifficulty, maxDifficulty, null, trialStatus, userId
            )
        }

        val problemIds = problems.mapNotNull { it.id }
        val trialStatuses = getTrialStatuses(problemIds, userId)

        return PageResponse.of(
            content = problems.map { ProblemResponse.from(it, trialStatuses[it.id]) },
            page = page,
            size = size,
            totalElements = totalElements
        )
    }

    @Transactional(readOnly = true)
    fun findById(problemId: Long, userId: UUID?): ProblemDetailResponse {
        val problem = getCachedProblem(problemId)
            ?: problemRepository.findById(problemId)?.also { cacheProblem(it) }
            ?: throw BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
        val trialStatus = getTrialStatus(problemId, userId)
        return ProblemDetailResponse.from(problem, trialStatus)
    }

    private fun getCachedProblem(id: Long): Problem? {
        return cacheService.get(CacheKeys.Problem.byId(id), Problem::class.java)
    }

    private fun cacheProblem(problem: Problem) {
        problem.id?.let {
            cacheService.put(CacheKeys.Problem.byId(it), problem, cacheProperties.ttl.problem)
        }
    }

    fun update(problemId: Long, request: UpdateProblemRequest) {
        val schemaSql = request.schemaMetadata?.let { SqlGenerator.generateSchema(it) }
        problemRepository.update(
            id = problemId,
            title = request.title,
            description = request.description,
            schemaSql = schemaSql,
            schemaMetadata = request.schemaMetadata,
            difficulty = request.difficulty,
            timeLimit = request.timeLimit,
            isOrderSensitive = request.isOrderSensitive
        ) ?: throw BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
        cacheService.evict(CacheKeys.Problem.byId(problemId))
        problemEventPublisher.publish(ProblemEvent.Updated(problemId))
    }

    fun delete(problemId: Long) {
        val deleted = problemRepository.softDelete(problemId)
        if (!deleted) {
            throw BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)
        }
        cacheService.evict(CacheKeys.Problem.byId(problemId))
        problemEventPublisher.publish(ProblemEvent.Deleted(problemId))
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

    private fun getTrialStatus(problemId: Long, userId: UUID?): TrialStatus {
        return submissionRepository.getTrialStatus(problemId, userId)
    }
}
