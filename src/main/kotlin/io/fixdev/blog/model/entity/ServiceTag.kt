package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "service_tags", schema = "blog")
class ServiceTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    var service: ServiceEntity,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
)
