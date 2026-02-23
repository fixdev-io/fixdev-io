package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "case_studies", schema = "blog")
class CaseStudy(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var industry: String? = null,

    @Column(nullable = false, length = 500)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false, length = 5)
    var locale: String = "ru",

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(nullable = false)
    var active: Boolean = true,

    @OneToMany(mappedBy = "caseStudy", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    var metrics: MutableList<CaseMetric> = mutableListOf()
)
