package me.suhyun.soj.domain.stats.presentation.response

data class StatsResponse(
    val problems: Long,
    val submissions: Long,
    val users: Long
)
