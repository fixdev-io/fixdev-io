package io.fixdev.blog.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HtmlSanitizerTest {
    private val sanitizer = HtmlSanitizer()

    @Test
    fun `allows safe HTML tags`() {
        val input = "<p>Hello <strong>world</strong></p>"
        assertThat(sanitizer.sanitize(input)).isEqualTo(input)
    }

    @Test
    fun `strips script tags`() {
        val input = "<p>Hello</p><script>alert('xss')</script>"
        assertThat(sanitizer.sanitize(input)).isEqualTo("<p>Hello</p>")
    }

    @Test
    fun `strips event handlers`() {
        val input = """<p onclick="alert('xss')">Hello</p>"""
        assertThat(sanitizer.sanitize(input)).isEqualTo("<p>Hello</p>")
    }
}
