package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.ArticleRevision
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRevisionRepository : JpaRepository<ArticleRevision, Long> {
    fun findByArticleIdOrderByVersionDesc(articleId: Long): List<ArticleRevision>
}
