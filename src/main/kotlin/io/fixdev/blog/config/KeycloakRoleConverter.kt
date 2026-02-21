package io.fixdev.blog.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component

@Component
class KeycloakRoleConverter {
    fun extractRoles(oidcUser: OidcUser): Collection<GrantedAuthority> {
        val realmAccess = oidcUser.claims["realm_access"] as? Map<*, *> ?: return emptyList()
        val roles = realmAccess["roles"] as? List<*> ?: return emptyList()
        return roles.filterIsInstance<String>()
            .filter { it == "ADMIN" || it == "USER" }
            .map { SimpleGrantedAuthority("ROLE_$it") }
    }
}
