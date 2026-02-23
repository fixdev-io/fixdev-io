package io.fixdev.blog.model.dto

import jakarta.validation.constraints.NotBlank

data class ArticleForm(
    @field:NotBlank
    val title: String = "",
    val content: String = "",
    val excerpt: String = "",
    val coverImageUrl: String = "",
    val seoTitle: String = "",
    val seoDescription: String = "",
    val tagIds: List<Long> = emptyList(),
    val publish: Boolean = false,
    val priority: Int = 0,
    val locale: String = "ru"
)
