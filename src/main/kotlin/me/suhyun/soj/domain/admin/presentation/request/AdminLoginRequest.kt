package me.suhyun.soj.domain.admin.presentation.request

import jakarta.validation.constraints.NotBlank

data class AdminLoginRequest(
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String
)
