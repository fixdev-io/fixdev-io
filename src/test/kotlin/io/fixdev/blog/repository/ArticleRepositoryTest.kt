package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class ArticleRepositoryTest @Autowired constructor(
    private val articleRepository: ArticleRepository
) {
    @Test
    fun `findByStatus returns only published articles`() {
        articleRepository.save(Article(title = "Draft", slug = "draft", status = ArticleStatus.DRAFT))
        articleRepository.save(Article(title = "Published", slug = "published", status = ArticleStatus.PUBLISHED))

        val result = articleRepository.findByStatus(ArticleStatus.PUBLISHED, PageRequest.of(0, 10))

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].slug).isEqualTo("published")
    }

    @Test
    fun `findBySlugAndStatus returns article when exists`() {
        articleRepository.save(Article(title = "Test", slug = "test-slug", status = ArticleStatus.PUBLISHED))

        val result = articleRepository.findBySlugAndStatus("test-slug", ArticleStatus.PUBLISHED)

        assertThat(result).isPresent
        assertThat(result.get().title).isEqualTo("Test")
    }
}
