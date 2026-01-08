package me.suhyun.soj.domain.problem.domain

import me.suhyun.soj.global.entity.BaseTable

@Suppress("MagicNumber")
object ProblemTable : BaseTable("problems") {
    val title = varchar("name", 128)
    val description = text("description")
    val schemaSql = text("schema_sql")
    val difficulty = integer(name = "difficulty")
    val timeLimit = integer("time_limit")
    val solvedCount = integer("solved_count").default(0)
    val submissionCount = integer("submitted_count").default(0)
}
