package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Comment
import io.fixdev.blog.model.entity.CommentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByArticleIdAndStatus(articleId: Long, status: CommentStatus): List<Comment>
    fun findByStatus(status: CommentStatus, pageable: Pageable): Page<Comment>
    fun countByStatus(status: CommentStatus): Long
}
