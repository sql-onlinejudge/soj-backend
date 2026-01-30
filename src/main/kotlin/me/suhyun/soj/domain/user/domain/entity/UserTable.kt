package me.suhyun.soj.domain.user.domain.entity

import me.suhyun.soj.domain.user.domain.model.enums.AuthProvider
import me.suhyun.soj.domain.user.domain.model.enums.UserRole
import me.suhyun.soj.global.common.entity.BaseTable

@Suppress("MagicNumber")
object UserTable : BaseTable("users") {
    val uuid = varchar("uuid", 36).uniqueIndex()
    val email = varchar("email", 255).nullable().uniqueIndex()
    val nickname = varchar("nickname", 100).nullable()
    val password = varchar("password", 255).nullable()
    val profileImageUrl = varchar("profile_image_url", 500).nullable()
    val provider = enumerationByName<AuthProvider>("provider", 20)
    val providerId = varchar("provider_id", 255).nullable()
    val role = enumerationByName<UserRole>("role", 20)
}
