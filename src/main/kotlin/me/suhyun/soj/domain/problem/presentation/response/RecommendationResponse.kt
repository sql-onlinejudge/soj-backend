package me.suhyun.soj.domain.problem.presentation.response

import me.suhyun.soj.domain.problem.domain.model.Problem

data class RecommendationResponse(
    val id: Long,
    val title: String,
    val difficulty: Int,
    val solvedCount: Int,
    val submissionCount: Int,
    val acceptanceRate: Double
) {
    companion object {
        fun from(problem: Problem) = RecommendationResponse(
            id = problem.id!!,
            title = problem.title,
            difficulty = problem.difficulty,
            solvedCount = problem.solvedCount,
            submissionCount = problem.submissionCount,
            acceptanceRate = if (problem.submissionCount == 0) 0.0
                             else problem.solvedCount.toDouble() / problem.submissionCount * 100
        )
    }
}
