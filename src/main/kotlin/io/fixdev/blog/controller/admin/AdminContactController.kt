package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.dto.ContactLinkForm
import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.model.entity.ContactLink
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.ContactLinkService
import io.fixdev.blog.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/contacts")
class AdminContactController(
    private val contactLinkService: ContactLinkService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("contacts", contactLinkService.findAll())
        return "admin/contacts/list"
    }

    @PostMapping("/new")
    fun create(@Valid @ModelAttribute("form") form: ContactLinkForm, result: BindingResult): String {
        if (result.hasErrors()) return "redirect:/admin/contacts"
        val link = ContactLink(
            icon = form.icon.ifBlank { null },
            label = form.label,
            url = form.url,
            locale = form.locale,
            sortOrder = form.sortOrder,
            active = form.active
        )
        val saved = contactLinkService.save(link)
        auditService.log(userService.getCurrentUser(), AuditAction.CREATE, AuditEntityType.CONTACT, saved.id, "Created contact: ${saved.label}")
        return "redirect:/admin/contacts"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        val link = contactLinkService.findById(id)
        contactLinkService.delete(id)
        auditService.log(userService.getCurrentUser(), AuditAction.DELETE, AuditEntityType.CONTACT, id, "Deleted contact: ${link?.label}")
        return "redirect:/admin/contacts"
    }
}
