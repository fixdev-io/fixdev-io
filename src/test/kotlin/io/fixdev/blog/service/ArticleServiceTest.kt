package io.fixdev.blog.service

import io.fixdev.blog.model.dto.ArticleForm
import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import io.fixdev.blog.repository.ArticleRepository
import io.fixdev.blog.repository.TagRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ArticleServiceTest {

    @Mock lateinit var articleRepository: ArticleRepository
    @Mock lateinit var tagRepository: TagRepository
    @Mock lateinit var slugService: SlugService
    @Mock lateinit var htmlSanitizer: HtmlSanitizer
    @Mock lateinit var articleRevisionService: ArticleRevisionService

    @InjectMocks lateinit var articleService: ArticleService

    @Test
    fun `create saves article with generated slug and sanitized content`() {
        val form = ArticleForm(title = "My Article", content = "<p>Hello</p>", publish = false)
        whenever(slugService.generate("My Article")).thenReturn("my-article")
        whenever(htmlSanitizer.sanitize("<p>Hello</p>")).thenReturn("<p>Hello</p>")
        whenever(articleRepository.save(any<Article>())).thenAnswer { it.arguments[0] }

        val result = articleService.create(form)

        assertThat(result.title).isEqualTo("My Article")
        assertThat(result.slug).isEqualTo("my-article")
        assertThat(result.status).isEqualTo(ArticleStatus.DRAFT)
    }

    @Test
    fun `create with publish flag sets status to PUBLISHED`() {
        val form = ArticleForm(title = "Published", content = "<p>Hi</p>", publish = true)
        whenever(slugService.generate(any())).thenReturn("published")
        whenever(htmlSanitizer.sanitize(any())).thenReturn("<p>Hi</p>")
        whenever(articleRepository.save(any<Article>())).thenAnswer { it.arguments[0] }

        val result = articleService.create(form)

        assertThat(result.status).isEqualTo(ArticleStatus.PUBLISHED)
        assertThat(result.publishedAt).isNotNull()
    }
}
