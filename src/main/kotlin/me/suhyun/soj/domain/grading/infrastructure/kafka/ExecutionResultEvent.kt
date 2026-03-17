package me.suhyun.soj.domain.grading.infrastructure.kafka

import me.suhyun.soj.domain.run.domain.model.enums.RunStatus

data class ExecutionResultEvent(
    val runId: Long,
    val status: RunStatus,
    val resultJson: String?,
    val errorMessage: String?
)
