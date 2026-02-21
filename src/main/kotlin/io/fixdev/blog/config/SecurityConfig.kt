package io.fixdev.blog.config

import io.fixdev.blog.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val keycloakRoleConverter: KeycloakRoleConverter,
    private val userService: UserService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/blog/*/comments").authenticated()
                    .anyRequest().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { it.oidcUserService(oidcUserService()) }
                    .defaultSuccessUrl("/", false)
            }
            .logout { it.logoutSuccessUrl("/") }

        return http.build()
    }

    private fun oidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
        val delegate = OidcUserService()
        return OAuth2UserService { request ->
            val oidcUser = delegate.loadUser(request)
            val roles = keycloakRoleConverter.extractRoles(oidcUser)
            userService.syncFromOidc(oidcUser)
            DefaultOidcUser(roles, oidcUser.idToken, oidcUser.userInfo)
        }
    }
}
