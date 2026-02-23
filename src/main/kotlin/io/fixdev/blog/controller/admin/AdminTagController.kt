package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.TagService
import io.fixdev.blog.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/tags")
class AdminTagController(
    private val tagService: TagService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("tags", tagService.findAll())
        return "admin/tags/list"
    }

    @PostMapping
    fun create(@RequestParam name: String): String {
        val tag = tagService.create(name)
        auditService.log(userService.getCurrentUser(), AuditAction.CREATE, AuditEntityType.TAG, tag.id, "Created tag: $name")
        return "redirect:/admin/tags"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        val tag = tagService.findById(id)
        tagService.delete(id)
        auditService.log(userService.getCurrentUser(), AuditAction.DELETE, AuditEntityType.TAG, id, "Deleted tag: ${tag?.name}")
        return "redirect:/admin/tags"
    }
}
