package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "article_revisions", schema = "blog")
class ArticleRevision(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    val article: Article,

    @Column(nullable = false)
    val version: Int,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val content: String? = null,

    @Column(length = 1000)
    val excerpt: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id")
    val editor: User? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
