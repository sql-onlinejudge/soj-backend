package me.suhyun.soj.domain.sandbox.presentation.response

data class SandboxQueryResponse(
    val columns: List<String>,
    val rows: List<List<String?>>
)
