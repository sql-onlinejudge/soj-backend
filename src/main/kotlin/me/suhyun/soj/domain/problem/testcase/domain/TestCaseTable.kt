package me.suhyun.soj.domain.problem.testcase.domain

import me.suhyun.soj.domain.problem.domain.ProblemTable
import me.suhyun.soj.global.entity.BaseTable

object TestCaseTable : BaseTable("test_cases") {
    val problemId = reference("problem_id", ProblemTable)
    val initSql = text("init_sql")
    val answer = text("answer")
}
