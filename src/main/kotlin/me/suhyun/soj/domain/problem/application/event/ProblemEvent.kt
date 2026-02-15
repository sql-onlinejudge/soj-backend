package me.suhyun.soj.domain.problem.application.event

sealed class ProblemEvent {
    abstract val problemId: Long

    data class Created(override val problemId: Long) : ProblemEvent()
    data class Updated(override val problemId: Long) : ProblemEvent()
    data class Deleted(override val problemId: Long) : ProblemEvent()
}
