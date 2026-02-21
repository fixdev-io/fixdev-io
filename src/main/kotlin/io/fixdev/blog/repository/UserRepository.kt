package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByKeycloakSub(keycloakSub: String): Optional<User>
}
