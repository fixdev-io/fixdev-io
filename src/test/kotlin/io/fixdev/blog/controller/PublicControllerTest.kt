package io.fixdev.blog.controller

import io.fixdev.blog.config.KeycloakRoleConverter
import io.fixdev.blog.config.SecurityConfig
import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import io.fixdev.blog.service.*
import java.time.Instant
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PublicController::class)
@ActiveProfiles("test")
@Import(SecurityConfig::class, KeycloakRoleConverter::class)
class PublicControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockitoBean lateinit var articleService: ArticleService
    @MockitoBean lateinit var commentService: CommentService
    @MockitoBean lateinit var tagService: TagService
    @MockitoBean lateinit var mediaService: MediaService
    @MockitoBean lateinit var userService: UserService
    @MockitoBean lateinit var homepageService: HomepageService
    @MockitoBean lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Test
    fun `GET blog returns blog list page`() {
        whenever(articleService.findPublished(any<String>(), any())).thenReturn(PageImpl(emptyList()))
        whenever(tagService.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/blog"))
            .andExpect(status().isOk)
            .andExpect(view().name("public/blog-list"))
    }

    @Test
    fun `GET blog article returns post page`() {
        val article = Article(id = 1, title = "Test", slug = "test", status = ArticleStatus.PUBLISHED, publishedAt = Instant.now())
        whenever(articleService.findPublishedBySlug("test")).thenReturn(article)
        whenever(commentService.findApprovedByArticle(1L)).thenReturn(emptyList())

        mockMvc.perform(get("/blog/test"))
            .andExpect(status().isOk)
            .andExpect(view().name("public/blog-post"))
            .andExpect(model().attributeExists("article"))
    }

    @Test
    fun `GET blog article returns 404 when not found`() {
        whenever(articleService.findPublishedBySlug("nonexistent")).thenReturn(null)

        mockMvc.perform(get("/blog/nonexistent"))
            .andExpect(status().isNotFound)
    }
}
