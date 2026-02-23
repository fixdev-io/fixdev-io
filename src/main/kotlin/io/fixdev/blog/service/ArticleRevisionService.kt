package io.fixdev.blog.service

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleRevision
import io.fixdev.blog.model.entity.User
import io.fixdev.blog.repository.ArticleRevisionRepository
import org.springframework.stereotype.Service

@Service
class ArticleRevisionService(private val articleRevisionRepository: ArticleRevisionRepository) {

    fun createSnapshot(article: Article, editor: User?) {
        val revision = ArticleRevision(
            article = article,
            version = article.version,
            title = article.title,
            content = article.content,
            excerpt = article.excerpt,
            editor = editor
        )
        articleRevisionRepository.save(revision)
    }

    fun findByArticleId(articleId: Long): List<ArticleRevision> =
        articleRevisionRepository.findByArticleIdOrderByVersionDesc(articleId)
}
