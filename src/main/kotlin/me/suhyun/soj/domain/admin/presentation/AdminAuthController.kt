package me.suhyun.soj.domain.admin.presentation

import jakarta.validation.Valid
import me.suhyun.soj.domain.admin.application.service.AdminAuthService
import me.suhyun.soj.domain.admin.presentation.request.AdminLoginRequest
import me.suhyun.soj.domain.admin.presentation.response.AdminLoginResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminAuthController(
    private val adminAuthService: AdminAuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: AdminLoginRequest): AdminLoginResponse {
        return adminAuthService.login(request)
    }
}
