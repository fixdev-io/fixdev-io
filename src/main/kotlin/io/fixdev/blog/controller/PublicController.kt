package io.fixdev.blog.controller

import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import io.fixdev.blog.service.MediaService
import io.fixdev.blog.service.TagService
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Controller
class PublicController(
    private val articleService: ArticleService,
    private val commentService: CommentService,
    private val tagService: TagService,
    private val mediaService: MediaService
) {
    @GetMapping("/")
    fun home(): String = "public/index"

    @GetMapping("/blog")
    fun blogList(
        @RequestParam(defaultValue = "0") page: Int,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, 10, Sort.by("publishedAt").descending())
        model.addAttribute("articles", articleService.findPublished(pageable))
        model.addAttribute("tags", tagService.findAll())
        return "public/blog-list"
    }

    @GetMapping("/blog/tag/{slug}")
    fun blogByTag(
        @PathVariable slug: String,
        @RequestParam(defaultValue = "0") page: Int,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, 10, Sort.by("publishedAt").descending())
        model.addAttribute("articles", articleService.findPublishedByTag(slug, pageable))
        model.addAttribute("tags", tagService.findAll())
        model.addAttribute("currentTag", tagService.findBySlug(slug))
        return "public/blog-list"
    }

    @GetMapping("/blog/{slug}")
    fun blogPost(@PathVariable slug: String, model: Model): String {
        val article = articleService.findPublishedBySlug(slug)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        model.addAttribute("article", article)
        model.addAttribute("comments", commentService.findApprovedByArticle(article.id))
        model.addAttribute("seoTitle", article.seoTitle ?: article.title)
        model.addAttribute("seoDescription", article.seoDescription ?: article.excerpt)
        return "public/blog-post"
    }

    @GetMapping("/media/{id}")
    fun serveMedia(@PathVariable id: Long): ResponseEntity<Resource> {
        val media = mediaService.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val path = mediaService.getFilePath(media)
        val resource = UrlResource(path.toUri())
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(media.mimeType))
            .body(resource)
    }
}
