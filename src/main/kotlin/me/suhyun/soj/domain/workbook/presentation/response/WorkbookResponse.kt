package me.suhyun.soj.domain.workbook.presentation.response

import me.suhyun.soj.domain.workbook.domain.model.Workbook
import java.time.LocalDateTime

data class WorkbookResponse(
    val id: Long?,
    val name: String,
    val description: String,
    val difficulty: Long,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun from(model: Workbook): WorkbookResponse {
            return WorkbookResponse(
                id = model.id,
                name = model.name,
                description = model.description,
                difficulty = model.difficulty,
                createdAt = model.createdAt,
            )
        }
    }
}