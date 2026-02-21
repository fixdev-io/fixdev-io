package io.fixdev.blog.controller

import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import io.fixdev.blog.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Controller
class CommentController(
    private val commentService: CommentService,
    private val articleService: ArticleService,
    private val userService: UserService
) {
    @PostMapping("/blog/{slug}/comments")
    fun addComment(
        @PathVariable slug: String,
        @RequestParam text: String,
        @AuthenticationPrincipal oidcUser: OidcUser,
        model: Model
    ): String {
        val article = articleService.findPublishedBySlug(slug)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val user = userService.findByKeycloakSub(oidcUser.subject)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        val comment = commentService.create(article, user, text)
        model.addAttribute("comment", comment)
        return "fragments/comment :: comment"
    }
}
