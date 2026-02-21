package io.fixdev.blog.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SlugServiceTest {
    private val slugService = SlugService()

    @Test
    fun `generates slug from English title`() {
        assertThat(slugService.generate("Hello World")).isEqualTo("hello-world")
    }

    @Test
    fun `generates slug from Russian title`() {
        assertThat(slugService.generate("Привет Мир")).isEqualTo("privet-mir")
    }

    @Test
    fun `removes special characters`() {
        assertThat(slugService.generate("Hello, World! #2")).isEqualTo("hello-world-2")
    }
}
