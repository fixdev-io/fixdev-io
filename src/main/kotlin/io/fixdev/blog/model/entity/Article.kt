package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "articles")
class Article(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, unique = true)
    var slug: String,

    @Column(columnDefinition = "TEXT")
    var content: String? = null,

    @Column(length = 1000)
    var excerpt: String? = null,

    @Column(name = "cover_image_url")
    var coverImageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ArticleStatus = ArticleStatus.DRAFT,

    @Column(name = "seo_title")
    var seoTitle: String? = null,

    @Column(name = "seo_description")
    var seoDescription: String? = null,

    @Column(name = "published_at")
    var publishedAt: Instant? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @ManyToMany
    @JoinTable(
        name = "article_tags",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),

    @OneToMany(mappedBy = "article")
    val comments: MutableList<Comment> = mutableListOf()
)
