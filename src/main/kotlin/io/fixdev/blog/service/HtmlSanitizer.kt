package io.fixdev.blog.service

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.stereotype.Service

@Service
class HtmlSanitizer {
    private val safelist = Safelist.relaxed()
        .addTags("pre", "code", "figure", "figcaption")
        .addAttributes("pre", "class")
        .addAttributes("code", "class")
        .addAttributes("img", "src", "alt", "title", "width", "height")

    fun sanitize(html: String): String =
        Jsoup.clean(html, safelist)
}
