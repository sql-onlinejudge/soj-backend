package me.suhyun.soj.domain.user.domain.repository

import me.suhyun.soj.domain.user.domain.model.User
import me.suhyun.soj.domain.user.domain.model.enums.AuthProvider
import java.util.UUID

interface UserRepository {
    fun save(user: User): User
    fun findById(id: Long): User?
    fun findByUuid(uuid: UUID): User?
    fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): User?
    fun findByEmail(email: String): User?
    fun update(user: User): User
    fun softDelete(uuid: UUID)
}
