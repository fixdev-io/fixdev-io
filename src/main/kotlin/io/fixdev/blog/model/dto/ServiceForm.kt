package io.fixdev.blog.model.dto

import jakarta.validation.constraints.NotBlank

data class ServiceForm(
    val icon: String = "",
    @field:NotBlank val title: String = "",
    val description: String = "",
    val locale: String = "ru",
    val sortOrder: Int = 0,
    val active: Boolean = true,
    val tags: String = ""
)
