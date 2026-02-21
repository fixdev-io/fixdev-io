package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.CommentService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/comments")
class AdminCommentController(private val commentService: CommentService) {

    @GetMapping
    fun list(@RequestParam(defaultValue = "0") page: Int, model: Model): String {
        model.addAttribute("comments", commentService.findPending(PageRequest.of(page, 20)))
        return "admin/comments/list"
    }

    @PostMapping("/{id}/approve")
    fun approve(@PathVariable id: Long): String {
        commentService.approve(id)
        return "redirect:/admin/comments"
    }

    @PostMapping("/{id}/reject")
    fun reject(@PathVariable id: Long): String {
        commentService.reject(id)
        return "redirect:/admin/comments"
    }
}
