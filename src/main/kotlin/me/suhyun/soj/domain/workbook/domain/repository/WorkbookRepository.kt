package me.suhyun.soj.domain.workbook.domain.repository

import me.suhyun.soj.domain.workbook.domain.model.Workbook

interface WorkbookRepository {
    fun save(workbook: Workbook): Workbook
    fun findById(id: Long): Workbook?
    fun findAll(
        page: Int,
        size: Int,
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        sort: List<String>,
    ): List<Workbook>

    fun countAll(
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
    ): Long
    fun update(id: Long, name: String?, description: String?, difficulty: Long?): Workbook?
    fun softDelete(id: Long): Boolean
}
