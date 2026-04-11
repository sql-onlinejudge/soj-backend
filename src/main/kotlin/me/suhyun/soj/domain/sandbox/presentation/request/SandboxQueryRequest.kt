package me.suhyun.soj.domain.sandbox.presentation.request

import jakarta.validation.constraints.NotBlank

data class SandboxQueryRequest(
    @field:NotBlank
    val query: String
)
