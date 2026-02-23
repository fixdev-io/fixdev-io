package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "services", schema = "blog")
class ServiceEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 100)
    var icon: String? = null,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false, length = 5)
    var locale: String = "ru",

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(nullable = false)
    var active: Boolean = true,

    @OneToMany(mappedBy = "service", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    var tags: MutableList<ServiceTag> = mutableListOf()
)
