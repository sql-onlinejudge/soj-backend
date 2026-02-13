package me.suhyun.soj.domain.testcase.domain.entity

import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata
import me.suhyun.soj.global.common.entity.BaseTable
import me.suhyun.soj.global.common.entity.json

object TestCaseTable : BaseTable("test_cases") {
    val problemId = long("problem_id")
    val initSql = text("init_sql").nullable()
    val initMetadata = json<InitMetadata>("init_metadata").nullable()
    val answer = text("answer")
    val answerMetadata = json<AnswerMetadata>("answer_metadata").nullable()
    val isVisible = bool("is_visible")
}
