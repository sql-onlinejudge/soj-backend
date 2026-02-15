package me.suhyun.soj.domain.problem.domain.repository

import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.SchemaMetadata
import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import java.util.UUID

interface ProblemRepository {
    fun save(problem: Problem): Problem
    fun findById(id: Long): Problem?
    fun findAll(
        page: Int,
        size: Int,
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        sort: List<String>,
        trialStatus: TrialStatus? = null,
        userId: UUID? = null
    ): List<Problem>
    fun countAll(
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        trialStatus: TrialStatus? = null,
        userId: UUID? = null
    ): Long
    fun update(
        id: Long,
        title: String?,
        description: String?,
        schemaSql: String?,
        schemaMetadata: SchemaMetadata?,
        difficulty: Int?,
        timeLimit: Int?,
        isOrderSensitive: Boolean?
    ): Problem?
    fun softDelete(id: Long): Boolean
    fun incrementSubmittedCount(id: Long)
    fun incrementSolvedCount(id: Long)
    fun findByIdsWithFilters(
        ids: List<Long>,
        minDifficulty: Int?,
        maxDifficulty: Int?,
        trialStatus: TrialStatus?,
        userId: UUID?,
        sort: List<String>
    ): List<Problem>
}
