package me.suhyun.soj.domain.user.domain.entity

import me.suhyun.soj.domain.user.domain.model.User
import me.suhyun.soj.domain.user.domain.model.enums.AuthProvider
import me.suhyun.soj.domain.user.domain.model.enums.UserRole
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var uuid: String by UserTable.uuid
    var email: String? by UserTable.email
    var nickname: String? by UserTable.nickname
    var password: String? by UserTable.password
    var profileImageUrl: String? by UserTable.profileImageUrl
    var provider: AuthProvider by UserTable.provider
    var providerId: String? by UserTable.providerId
    var role: UserRole by UserTable.role
    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
    var deletedAt by UserTable.deletedAt

    fun toModel(): User = User(
        id = id.value,
        uuid = UUID.fromString(uuid),
        email = email,
        nickname = nickname,
        password = password,
        profileImageUrl = profileImageUrl,
        provider = provider,
        providerId = providerId,
        role = role,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
