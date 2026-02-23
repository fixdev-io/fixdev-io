package io.fixdev.blog.config

import io.fixdev.blog.service.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
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
    @MockitoBean lateinit var userService: UserService
    @MockitoBean lateinit var articleService: ArticleService
    @MockitoBean lateinit var commentService: CommentService
    @MockitoBean lateinit var tagService: TagService
    @MockitoBean lateinit var mediaService: MediaService
    @MockitoBean lateinit var homepageService: HomepageService
    @MockitoBean lateinit var auditService: AuditService
    @MockitoBean lateinit var articleRevisionService: ArticleRevisionService
    @MockitoBean lateinit var serviceEntityService: ServiceEntityService
    @MockitoBean lateinit var caseStudyService: CaseStudyService
    @MockitoBean lateinit var contactLinkService: ContactLinkService
    @MockitoBean lateinit var techTagService: TechTagService
    @MockitoBean lateinit var pageBlockService: PageBlockService
    @MockitoBean lateinit var clientRegistrationRepository: ClientRegistrationRepository

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
        whenever(articleService.findAll(any())).thenReturn(PageImpl(emptyList()))

        mockMvc.perform(
            get("/admin/articles")
                .with(oidcLogin().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk)
    }
}
