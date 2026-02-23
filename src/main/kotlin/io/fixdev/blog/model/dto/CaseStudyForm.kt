package io.fixdev.blog.model.dto

import jakarta.validation.constraints.NotBlank

data class CaseStudyForm(
    val industry: String = "",
    @field:NotBlank val title: String = "",
    val description: String = "",
    val locale: String = "ru",
    val sortOrder: Int = 0,
    val active: Boolean = true,
    val metricValues: List<String> = emptyList(),
    val metricLabels: List<String> = emptyList()
)
