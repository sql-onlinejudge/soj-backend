package me.suhyun.soj.domain.workbook.domain.entity

import me.suhyun.soj.global.common.entity.BaseTable

@Suppress("MagicNumber")
object WorkbookTable : BaseTable("workbooks") {
    val name = varchar("name", 128)
    val description = text("description")
    val difficulty = long("difficulty").default(3)
}
