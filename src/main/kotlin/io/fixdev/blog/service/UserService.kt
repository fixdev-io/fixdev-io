package io.fixdev.blog.service

import io.fixdev.blog.model.entity.User
import io.fixdev.blog.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun syncFromOidc(oidcUser: OidcUser): User {
        val sub = oidcUser.subject
        return userRepository.findByKeycloakSub(sub).orElseGet {
            userRepository.save(
                User(
                    keycloakSub = sub,
                    name = oidcUser.fullName ?: oidcUser.preferredUsername ?: "Anonymous",
                    email = oidcUser.email,
                    avatarUrl = oidcUser.picture
                )
            )
        }
    }

    fun findByKeycloakSub(sub: String): User? =
        userRepository.findByKeycloakSub(sub).orElse(null)

    fun getCurrentUser(): User? {
        val auth = SecurityContextHolder.getContext().authentication ?: return null
        val principal = auth.principal
        if (principal is OidcUser) {
            return findByKeycloakSub(principal.subject)
        }
        return null
    }
}
