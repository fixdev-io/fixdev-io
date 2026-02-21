package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "media")
class Media(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val filename: String,

    @Column(name = "stored_path", nullable = false)
    val storedPath: String,

    @Column(name = "mime_type", nullable = false)
    val mimeType: String,

    @Column(name = "size_bytes", nullable = false)
    val sizeBytes: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    var article: Article? = null,

    @Column(name = "uploaded_at", nullable = false)
    val uploadedAt: Instant = Instant.now()
)
