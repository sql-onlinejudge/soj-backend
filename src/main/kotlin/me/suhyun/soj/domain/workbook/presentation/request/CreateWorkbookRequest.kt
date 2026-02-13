package me.suhyun.soj.domain.workbook.presentation.request

data class CreateWorkbookRequest(
    val name: String,
    val description: String,
    val difficulty: Long,
)
