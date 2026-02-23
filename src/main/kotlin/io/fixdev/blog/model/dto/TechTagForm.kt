package io.fixdev.blog.model.dto

import jakarta.validation.constraints.NotBlank

data class TechTagForm(
    @field:NotBlank val name: String = "",
    val accent: Boolean = false,
    val locale: String = "ru",
    val sortOrder: Int = 0
)
