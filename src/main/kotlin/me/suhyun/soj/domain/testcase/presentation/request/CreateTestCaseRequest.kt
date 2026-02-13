package me.suhyun.soj.domain.testcase.presentation.request

import jakarta.validation.constraints.NotNull
import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata

data class CreateTestCaseRequest(
    val initData: InitMetadata?,

    @field:NotNull
    val answerData: AnswerMetadata,

    val isVisible: Boolean,
)
