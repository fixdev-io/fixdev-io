package io.fixdev.blog.model.entity

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class ArticleTest {
    @Test
    fun `new article defaults to DRAFT status`() {
        val article = Article(title = "Test", slug = "test")
        assertThat(article.status).isEqualTo(ArticleStatus.DRAFT)
    }

    @Test
    fun `new article has empty tags`() {
        val article = Article(title = "Test", slug = "test")
        assertThat(article.tags).isEmpty()
    }
}
