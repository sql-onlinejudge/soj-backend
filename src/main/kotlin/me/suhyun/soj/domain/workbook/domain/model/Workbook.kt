package me.suhyun.soj.domain.workbook.domain.model

import me.suhyun.soj.domain.workbook.domain.entity.WorkbookEntity
import java.time.LocalDateTime

data class Workbook(
    val id: Long?,
    val name: String,
    val description: String,
    val difficulty: Long,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?,
) {
    companion object {
        fun from(entity: WorkbookEntity) = Workbook(
            id = entity.id.value,
            name = entity.name,
            description = entity.description,
            difficulty = entity.difficulty,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt,
        )
    }
}
