package me.suhyun.soj.domain.stats.application.service

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.stats.presentation.response.StatsResponse
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StatsService(
    private val problemRepository: ProblemRepository,
    private val submissionRepository: SubmissionRepository,
    private val userRepository: UserRepository
) {

    fun getStats(): StatsResponse = StatsResponse(
        problems = problemRepository.countAll(null, null, null),
        submissions = submissionRepository.countAll(),
        users = userRepository.countAll()
    )
}
