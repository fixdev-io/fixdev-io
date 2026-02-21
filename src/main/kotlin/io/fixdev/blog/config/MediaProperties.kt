package io.fixdev.blog.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.media")
data class MediaProperties(
    val uploadDir: String = "./uploads",
    val allowedTypes: List<String> = listOf("image/jpeg", "image/png", "image/webp"),
    val maxSizeBytes: Long = 10_485_760
)
