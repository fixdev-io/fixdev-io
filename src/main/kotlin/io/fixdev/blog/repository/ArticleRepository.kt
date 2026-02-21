package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findBySlugAndStatus(slug: String, status: ArticleStatus): Optional<Article>
    fun findByStatus(status: ArticleStatus, pageable: Pageable): Page<Article>
    fun findByTagsSlugAndStatus(tagSlug: String, status: ArticleStatus, pageable: Pageable): Page<Article>
    fun countByStatus(status: ArticleStatus): Long
}
