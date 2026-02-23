package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "page_blocks", schema = "blog")
class PageBlock(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 50)
    var page: String,

    @Column(nullable = false, length = 50)
    var section: String,

    @Column(name = "\"key\"", nullable = false, length = 100)
    var key: String,

    @Column(name = "\"value\"", columnDefinition = "TEXT")
    var value: String? = null,

    @Column(nullable = false, length = 5)
    var locale: String = "ru",

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
)
