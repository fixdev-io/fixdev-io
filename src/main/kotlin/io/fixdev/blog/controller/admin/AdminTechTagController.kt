package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.dto.TechTagForm
import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.model.entity.TechTag
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.TechTagService
import io.fixdev.blog.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/tech-tags")
class AdminTechTagController(
    private val techTagService: TechTagService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("techTags", techTagService.findAll())
        return "admin/tech-tags/list"
    }

    @PostMapping("/new")
    fun create(@Valid @ModelAttribute("form") form: TechTagForm, result: BindingResult): String {
        if (result.hasErrors()) return "redirect:/admin/tech-tags"
        val tag = TechTag(name = form.name, accent = form.accent, locale = form.locale, sortOrder = form.sortOrder)
        val saved = techTagService.save(tag)
        auditService.log(userService.getCurrentUser(), AuditAction.CREATE, AuditEntityType.TECH_TAG, saved.id, "Created tech tag: ${saved.name}")
        return "redirect:/admin/tech-tags"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        val tag = techTagService.findById(id)
        techTagService.delete(id)
        auditService.log(userService.getCurrentUser(), AuditAction.DELETE, AuditEntityType.TECH_TAG, id, "Deleted tech tag: ${tag?.name}")
        return "redirect:/admin/tech-tags"
    }
}
