package me.suhyun.soj.domain.workbook.presentation.request

data class UpdateWorkbookRequest(
    val name: String,
    val description: String,
    val difficulty: Long,
)
