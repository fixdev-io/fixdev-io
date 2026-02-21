package io.fixdev.blog.config

import io.fixdev.blog.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
@ActiveProfiles("test")
@Import(SecurityConfig::class, KeycloakRoleConverter::class)
class SecurityConfigTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Test
    fun `admin pages require authentication`() {
        mockMvc.perform(get("/admin/articles"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    fun `admin pages reject non-admin users`() {
        mockMvc.perform(
            get("/admin/articles")
                .with(oidcLogin().authorities(SimpleGrantedAuthority("ROLE_USER")))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `admin pages accessible with ADMIN role`() {
        val result = mockMvc.perform(
            get("/admin/articles")
                .with(oidcLogin().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andReturn()
        assert(result.response.status != 403)
        assert(result.response.status != 302)
    }
}
