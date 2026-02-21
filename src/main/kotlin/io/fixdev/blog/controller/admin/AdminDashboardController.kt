package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminDashboardController(
    private val articleService: ArticleService,
    private val commentService: CommentService
) {
    @GetMapping
    fun dashboard(model: Model): String {
        model.addAttribute("publishedCount", articleService.countPublished())
        model.addAttribute("draftsCount", articleService.countDrafts())
        model.addAttribute("pendingCommentsCount", commentService.countPending())
        return "admin/dashboard"
    }
}
