package io.fixdev.blog.service

import io.fixdev.blog.model.entity.*
import io.fixdev.blog.repository.CommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(private val commentRepository: CommentRepository) {

    fun findApprovedByArticle(articleId: Long): List<Comment> =
        commentRepository.findByArticleIdAndStatus(articleId, CommentStatus.APPROVED)

    fun findPending(pageable: Pageable): Page<Comment> =
        commentRepository.findByStatus(CommentStatus.PENDING, pageable)

    fun countPending(): Long =
        commentRepository.countByStatus(CommentStatus.PENDING)

    @Transactional
    fun create(article: Article, user: User, text: String): Comment =
        commentRepository.save(Comment(article = article, user = user, text = text))

    @Transactional
    fun approve(id: Long): Comment {
        val comment = commentRepository.findById(id).orElseThrow()
        comment.status = CommentStatus.APPROVED
        return commentRepository.save(comment)
    }

    @Transactional
    fun reject(id: Long): Comment {
        val comment = commentRepository.findById(id).orElseThrow()
        comment.status = CommentStatus.REJECTED
        return commentRepository.save(comment)
    }

    fun delete(id: Long) = commentRepository.deleteById(id)
}
