package me.suhyun.soj.domain.grading.infrastructure.kafka

import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict

data class GradingResultEvent(
    val submissionId: Long,
    val status: SubmissionStatus,
    val verdict: SubmissionVerdict?
)
