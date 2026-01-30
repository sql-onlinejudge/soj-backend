package me.suhyun.soj.domain.admin.presentation.response

data class AdminLoginResponse(
    val accessToken: String,
    val refreshToken: String
)
