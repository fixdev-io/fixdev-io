package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "comments", schema = "blog")
class Comment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    val article: Article,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, columnDefinition = "TEXT")
    var text: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CommentStatus = CommentStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
