package me.suhyun.soj.domain.admin.application.service

import me.suhyun.soj.domain.admin.exception.AdminErrorCode
import me.suhyun.soj.domain.admin.presentation.request.AdminLoginRequest
import me.suhyun.soj.domain.admin.presentation.response.AdminLoginResponse
import me.suhyun.soj.domain.user.application.service.UserService
import me.suhyun.soj.domain.user.domain.model.enums.UserRole
import me.suhyun.soj.global.exception.BusinessException
import me.suhyun.soj.global.security.jwt.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdminAuthService(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(request: AdminLoginRequest): AdminLoginResponse {
        val user = userService.findByEmail(request.email)
            ?: throw BusinessException(AdminErrorCode.INVALID_CREDENTIALS)

        if (user.role != UserRole.ADMIN) {
            throw BusinessException(AdminErrorCode.INVALID_CREDENTIALS)
        }

        if (user.password == null || !passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(AdminErrorCode.INVALID_CREDENTIALS)
        }

        val accessToken = jwtTokenProvider.createAccessToken(user)
        val refreshToken = jwtTokenProvider.createRefreshToken(user)

        return AdminLoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
