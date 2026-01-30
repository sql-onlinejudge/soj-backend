package me.suhyun.soj.domain.user.application.service

import me.suhyun.soj.domain.user.domain.model.User
import me.suhyun.soj.domain.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository
) {

    fun findById(id: Long): User? = userRepository.findById(id)

    fun findByUuid(uuid: UUID): User? = userRepository.findByUuid(uuid)

    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    @Transactional
    fun save(user: User): User = userRepository.save(user)

    @Transactional
    fun softDelete(uuid: UUID) {
        userRepository.softDelete(uuid)
    }
}
