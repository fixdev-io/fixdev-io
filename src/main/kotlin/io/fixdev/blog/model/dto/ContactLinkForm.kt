package io.fixdev.blog.model.dto

import jakarta.validation.constraints.NotBlank

data class ContactLinkForm(
    val icon: String = "",
    @field:NotBlank val label: String = "",
    @field:NotBlank val url: String = "",
    val locale: String = "ru",
    val sortOrder: Int = 0,
    val active: Boolean = true
)
