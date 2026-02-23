package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "tech_tags", schema = "blog")
class TechTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false)
    var accent: Boolean = false,

    @Column(nullable = false, length = 5)
    var locale: String = "ru",

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
)
