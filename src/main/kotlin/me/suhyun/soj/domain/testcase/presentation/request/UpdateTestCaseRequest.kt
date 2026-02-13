package me.suhyun.soj.domain.testcase.presentation.request

import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata

data class UpdateTestCaseRequest(
    val initData: InitMetadata?,
    val answerData: AnswerMetadata?,
    val isVisible: Boolean? = null
)
