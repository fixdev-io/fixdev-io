package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import io.fixdev.blog.infra.TestcontainerDbContextInitializer
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [TestcontainerDbContextInitializer::class])
@Transactional
class ArticleRepositoryTest @Autowired constructor(
    private val articleRepository: ArticleRepository
) {
    @Test
    fun `findByStatus returns only published articles`() {
        articleRepository.save(Article(title = "Draft", slug = "draft-${System.nanoTime()}", status = ArticleStatus.DRAFT))
        articleRepository.save(Article(title = "Published", slug = "published-${System.nanoTime()}", status = ArticleStatus.PUBLISHED))

        val result = articleRepository.findByStatus(ArticleStatus.PUBLISHED, PageRequest.of(0, 100))

        assertThat(result.content.any { it.title == "Published" }).isTrue()
    }

    @Test
    fun `findBySlugAndStatus returns article when exists`() {
        val slug = "test-slug-${System.nanoTime()}"
        articleRepository.save(Article(title = "Test", slug = slug, status = ArticleStatus.PUBLISHED))

        val result = articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)

        assertThat(result).isPresent
        assertThat(result.get().title).isEqualTo("Test")
    }
}
