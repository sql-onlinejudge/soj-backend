package me.suhyun.soj.domain.problem.presentation.response

import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.SchemaMetadata
import me.suhyun.soj.domain.problem.domain.model.enums.ProblemCategory
import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import java.time.LocalDateTime

data class ProblemDetailResponse(
    val id: Long,
    val title: String,
    val description: String,
    val schemaSql: String,
    val schemaMetadata: SchemaMetadata?,
    val difficulty: Int,
    val timeLimit: Int,
    val isOrderSensitive: Boolean,
    val solvedCount: Int,
    val submissionCount: Int,
    val category: ProblemCategory,
    val trialStatus: TrialStatus?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(problem: Problem, trialStatus: TrialStatus? = null): ProblemDetailResponse {
            return ProblemDetailResponse(
                id = problem.id!!,
                title = problem.title,
                description = problem.description,
                schemaSql = problem.schemaSql,
                schemaMetadata = problem.schemaMetadata,
                difficulty = problem.difficulty,
                timeLimit = problem.timeLimit,
                isOrderSensitive = problem.isOrderSensitive,
                solvedCount = problem.solvedCount,
                submissionCount = problem.submissionCount,
                category = problem.category,
                trialStatus = trialStatus,
                createdAt = problem.createdAt,
                updatedAt = problem.updatedAt
            )
        }
    }
}
