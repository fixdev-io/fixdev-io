package io.fixdev.blog.service

import io.fixdev.blog.model.dto.ArticleForm
import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import io.fixdev.blog.repository.ArticleRepository
import io.fixdev.blog.repository.TagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val tagRepository: TagRepository,
    private val slugService: SlugService,
    private val htmlSanitizer: HtmlSanitizer
) {
    fun findPublished(pageable: Pageable): Page<Article> =
        articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageable)

    fun findPublishedByTag(tagSlug: String, pageable: Pageable): Page<Article> =
        articleRepository.findByTagsSlugAndStatus(tagSlug, ArticleStatus.PUBLISHED, pageable)

    fun findPublishedBySlug(slug: String): Article? =
        articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED).orElse(null)

    fun findAll(pageable: Pageable): Page<Article> =
        articleRepository.findAll(pageable)

    fun findById(id: Long): Article? =
        articleRepository.findById(id).orElse(null)

    @Transactional
    fun create(form: ArticleForm): Article {
        val article = Article(
            title = form.title,
            slug = slugService.generate(form.title),
            content = htmlSanitizer.sanitize(form.content),
            excerpt = form.excerpt.ifBlank { null },
            coverImageUrl = form.coverImageUrl.ifBlank { null },
            seoTitle = form.seoTitle.ifBlank { null },
            seoDescription = form.seoDescription.ifBlank { null },
            status = if (form.publish) ArticleStatus.PUBLISHED else ArticleStatus.DRAFT,
            publishedAt = if (form.publish) Instant.now() else null
        )
        if (form.tagIds.isNotEmpty()) {
            article.tags = tagRepository.findAllById(form.tagIds).toMutableSet()
        }
        return articleRepository.save(article)
    }

    @Transactional
    fun update(id: Long, form: ArticleForm): Article {
        val article = articleRepository.findById(id).orElseThrow()
        article.title = form.title
        article.content = htmlSanitizer.sanitize(form.content)
        article.excerpt = form.excerpt.ifBlank { null }
        article.coverImageUrl = form.coverImageUrl.ifBlank { null }
        article.seoTitle = form.seoTitle.ifBlank { null }
        article.seoDescription = form.seoDescription.ifBlank { null }
        article.updatedAt = Instant.now()
        if (form.publish && article.status == ArticleStatus.DRAFT) {
            article.status = ArticleStatus.PUBLISHED
            article.publishedAt = Instant.now()
        }
        article.tags = if (form.tagIds.isNotEmpty()) {
            tagRepository.findAllById(form.tagIds).toMutableSet()
        } else {
            mutableSetOf()
        }
        return articleRepository.save(article)
    }

    fun delete(id: Long) = articleRepository.deleteById(id)

    fun countPublished(): Long = articleRepository.countByStatus(ArticleStatus.PUBLISHED)
    fun countDrafts(): Long = articleRepository.countByStatus(ArticleStatus.DRAFT)
}
