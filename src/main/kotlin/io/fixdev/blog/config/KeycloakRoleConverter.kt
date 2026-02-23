package io.fixdev.blog.config

import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component

@Component
class KeycloakRoleConverter {

    private val log = LoggerFactory.getLogger(KeycloakRoleConverter::class.java)

    fun extractRoles(oidcUser: OidcUser): Collection<GrantedAuthority> {
        log.info("All claims: {}", oidcUser.claims.keys)
        log.info("realm_access claim: {}, type: {}", oidcUser.claims["realm_access"], oidcUser.claims["realm_access"]?.javaClass)
        val roles = extractRoleNames(oidcUser)
        val authorities = roles
            .filter { it == "ADMIN" || it == "USER" }
            .map { SimpleGrantedAuthority("ROLE_$it") }
        log.info("Extracted authorities: {}", authorities)
        return authorities
    }

    private fun extractRoleNames(oidcUser: OidcUser): List<String> {
        val realmAccess = oidcUser.claims["realm_access"]
        if (realmAccess is Map<*, *>) {
            val roles = realmAccess["roles"] as? List<*>
            if (roles != null) return roles.filterIsInstance<String>()
        }
        if (realmAccess is List<*>) {
            return realmAccess.filterIsInstance<String>()
        }
        return emptyList()
    }
}
