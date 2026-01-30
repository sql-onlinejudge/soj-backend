package me.suhyun.soj.domain.user.domain.model

import me.suhyun.soj.domain.user.domain.model.enums.AuthProvider
import me.suhyun.soj.domain.user.domain.model.enums.UserRole
import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: Long? = null,
    val uuid: UUID,
    val email: String? = null,
    val nickname: String? = null,
    val password: String? = null,
    val profileImageUrl: String? = null,
    val provider: AuthProvider,
    val providerId: String? = null,
    val role: UserRole,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
) {
    fun isAdmin(): Boolean = role == UserRole.ADMIN

    companion object {
        fun createAdmin(
            email: String,
            nickname: String,
            password: String
        ): User = User(
            uuid = UUID.randomUUID(),
            email = email,
            nickname = nickname,
            password = password,
            provider = AuthProvider.LOCAL,
            role = UserRole.ADMIN
        )
    }
}
