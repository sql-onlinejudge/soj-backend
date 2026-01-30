package me.suhyun.soj.domain.user.domain.repository

import me.suhyun.soj.domain.user.domain.entity.UserEntity
import me.suhyun.soj.domain.user.domain.entity.UserTable
import me.suhyun.soj.domain.user.domain.model.User
import me.suhyun.soj.domain.user.domain.model.enums.AuthProvider
import org.jetbrains.exposed.sql.and
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Repository
@Transactional
class UserRepositoryImpl : UserRepository {

    override fun save(user: User): User {
        val entity = UserEntity.new {
            uuid = user.uuid.toString()
            email = user.email
            nickname = user.nickname
            password = user.password
            profileImageUrl = user.profileImageUrl
            provider = user.provider
            providerId = user.providerId
            role = user.role
            createdAt = user.createdAt
            updatedAt = user.updatedAt
            deletedAt = user.deletedAt
        }
        return entity.toModel()
    }

    override fun findById(id: Long): User? {
        return UserEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?.toModel()
    }

    override fun findByUuid(uuid: UUID): User? {
        return UserEntity.find {
            (UserTable.uuid eq uuid.toString()) and (UserTable.deletedAt.isNull())
        }.firstOrNull()?.toModel()
    }

    override fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): User? {
        return UserEntity.find {
            (UserTable.provider eq provider) and
                (UserTable.providerId eq providerId) and
                (UserTable.deletedAt.isNull())
        }.firstOrNull()?.toModel()
    }

    override fun findByEmail(email: String): User? {
        return UserEntity.find {
            (UserTable.email eq email) and (UserTable.deletedAt.isNull())
        }.firstOrNull()?.toModel()
    }

    override fun update(user: User): User {
        val entity = UserEntity.find {
            UserTable.uuid eq user.uuid.toString()
        }.firstOrNull() ?: throw IllegalArgumentException("User not found: ${user.uuid}")

        entity.email = user.email
        entity.nickname = user.nickname
        entity.profileImageUrl = user.profileImageUrl
        entity.provider = user.provider
        entity.providerId = user.providerId
        entity.role = user.role
        entity.updatedAt = LocalDateTime.now()

        return entity.toModel()
    }

    override fun softDelete(uuid: UUID) {
        UserEntity.find {
            UserTable.uuid eq uuid.toString()
        }.firstOrNull()?.let {
            it.deletedAt = LocalDateTime.now()
        }
    }
}
