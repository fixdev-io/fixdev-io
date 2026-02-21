package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false, unique = true)
    var slug: String
)
