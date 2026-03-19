package me.suhyun.soj.domain.problem.application.service

import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.exception.ProblemErrorCode
import me.suhyun.soj.domain.problem.presentation.request.RecommendationTrigger
import me.suhyun.soj.domain.problem.presentation.response.RecommendationResponse
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.global.exception.BusinessException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

@Service
@Transactional(readOnly = true)
class RecommendationService(
    private val problemRepository: ProblemRepository,
    private val submissionRepository: SubmissionRepository
) {

    fun recommend(problemId: Long, trigger: RecommendationTrigger): List<RecommendationResponse> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        val currentProblem = problemRepository.findById(problemId)
            ?: throw BusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)

        val wrongCount = submissionRepository.countNonAcceptedByUserIdAndProblemId(userId, problemId)
        val skill = estimateSkill(userId)
        val target = calcTarget(currentProblem.difficulty, wrongCount, skill, trigger)

        val solvedIds = submissionRepository.findSolvedProblemIdsByUserId(userId)
        val candidates = problemRepository.findAllActive()
            .filter { it.id !in solvedIds && it.id != problemId }

        val trialStatuses = submissionRepository.getTrialStatuses(candidates.map { it.id!! }, userId)

        fun score(problem: Problem): Double {
            val diffDist = abs(problem.difficulty - target)
            val diffScore = 100.0 / (1 + diffDist.pow(1.5))
            val popularityBonus = minOf(20.0, ln(problem.solvedCount + 1.0) * 5)
            val retryPenalty = if (trialStatuses[problem.id] == false) 30.0 else 0.0
            return diffScore + popularityBonus - retryPenalty
        }

        val scored = candidates.map { it to score(it) }
            .sortedByDescending { it.second }

        val nearPicks = scored.take(2).map { it.first }

        val challengePick = scored
            .filter { (p, _) -> p.difficulty >= target + 1 && p.difficulty <= target + 3 }
            .firstOrNull { (p, _) -> p.id !in nearPicks.map { it.id } }
            ?.first

        return (nearPicks + listOfNotNull(challengePick)).map { RecommendationResponse.from(it) }
    }

    private fun estimateSkill(userId: UUID): Double {
        val recentSubmissions = submissionRepository.findRecentByUserId(userId, 100)
        val byProblem = recentSubmissions.groupBy { it.problemId }

        val solvedProblemIds = byProblem
            .filter { (_, subs) -> subs.any { it.verdict == SubmissionVerdict.ACCEPTED } }
            .keys.toList()

        if (solvedProblemIds.isEmpty()) return 5.0

        val problems = problemRepository.findByIdsWithFilters(
            ids = solvedProblemIds,
            minDifficulty = null, maxDifficulty = null,
            trialStatus = null, userId = null,
            sort = listOf("id:asc")
        ).associateBy { it.id }

        val contributions = solvedProblemIds.mapNotNull { pid ->
            val problem = problems[pid] ?: return@mapNotNull null
            val subs = byProblem[pid] ?: return@mapNotNull null
            val wrongCount = subs.count { it.verdict != null && it.verdict != SubmissionVerdict.ACCEPTED }
            problem.difficulty.toDouble() / (1 + ln(wrongCount + 1.0))
        }

        return if (contributions.isEmpty()) 5.0 else contributions.average()
    }

    private fun calcTarget(difficulty: Int, wrongCount: Int, skill: Double, trigger: RecommendationTrigger): Double {
        val delta = when (trigger) {
            RecommendationTrigger.SOLVED -> maxOf(0, 2 - wrongCount / 3)
            RecommendationTrigger.LEAVING -> -minOf(2, maxOf(0, wrongCount - 1) / 2)
        }
        val raw = difficulty + delta + 0.3 * (skill - difficulty)
        return raw.coerceIn(1.0, 20.0)
    }
}
