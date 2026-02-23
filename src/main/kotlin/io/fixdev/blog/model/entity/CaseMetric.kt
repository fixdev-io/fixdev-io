package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "case_metrics", schema = "blog")
class CaseMetric(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_study_id", nullable = false)
    var caseStudy: CaseStudy,

    @Column(name = "metric_value", nullable = false, length = 50)
    var value: String,

    @Column(nullable = false, length = 100)
    var label: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
)
