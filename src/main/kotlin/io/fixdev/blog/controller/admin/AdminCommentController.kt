package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.CommentService
import io.fixdev.blog.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/comments")
class AdminCommentController(
    private val commentService: CommentService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun list(@RequestParam(defaultValue = "0") page: Int, model: Model): String {
        model.addAttribute("comments", commentService.findPending(PageRequest.of(page, 20)))
        return "admin/comments/list"
    }

    @PostMapping("/{id}/approve")
    fun approve(@PathVariable id: Long): String {
        commentService.approve(id)
        auditService.log(userService.getCurrentUser(), AuditAction.APPROVE, AuditEntityType.COMMENT, id, "Approved comment")
        return "redirect:/admin/comments"
    }

    @PostMapping("/{id}/reject")
    fun reject(@PathVariable id: Long): String {
        commentService.reject(id)
        auditService.log(userService.getCurrentUser(), AuditAction.REJECT, AuditEntityType.COMMENT, id, "Rejected comment")
        return "redirect:/admin/comments"
    }
}
