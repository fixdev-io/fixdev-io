package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface ArticleRepository : JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.tags WHERE a.id = :id")
    fun findByIdWithTags(id: Long): Article?

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.tags WHERE a.slug = :slug AND a.status = :status")
    fun findBySlugAndStatus(slug: String, status: ArticleStatus): Optional<Article>

    @EntityGraph(attributePaths = ["tags"])
    fun findByStatus(status: ArticleStatus, pageable: Pageable): Page<Article>

    @EntityGraph(attributePaths = ["tags"])
    fun findByTagsSlugAndStatus(tagSlug: String, status: ArticleStatus, pageable: Pageable): Page<Article>

    fun countByStatus(status: ArticleStatus): Long

    @EntityGraph(attributePaths = ["tags"])
    fun findByStatusAndLocale(status: ArticleStatus, locale: String, pageable: Pageable): Page<Article>

    @EntityGraph(attributePaths = ["tags"])
    fun findByTagsSlugAndStatusAndLocale(tagSlug: String, status: ArticleStatus, locale: String, pageable: Pageable): Page<Article>

    @EntityGraph(attributePaths = ["tags"])
    override fun findAll(pageable: Pageable): Page<Article>

    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    fun incrementViewCount(id: Long)
}
