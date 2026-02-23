package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "contact_links", schema = "blog")
class ContactLink(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 100)
    var icon: String? = null,

    @Column(nullable = false)
    var label: String,

    @Column(nullable = false, length = 500)
    var url: String,

    @Column(nullable = false, length = 5)
    var locale: String = "ru",

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(nullable = false)
    var active: Boolean = true
)
