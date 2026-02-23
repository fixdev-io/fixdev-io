package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.dto.ServiceForm
import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.model.entity.ServiceEntity
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.ServiceEntityService
import io.fixdev.blog.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/services")
class AdminServiceController(
    private val serviceEntityService: ServiceEntityService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("services", serviceEntityService.findAll())
        return "admin/services/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("form", ServiceForm())
        return "admin/services/form"
    }

    @PostMapping("/new")
    fun create(@Valid @ModelAttribute("form") form: ServiceForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) return "admin/services/form"
        val entity = ServiceEntity(
            icon = form.icon.ifBlank { null },
            title = form.title,
            description = form.description.ifBlank { null },
            locale = form.locale,
            sortOrder = form.sortOrder,
            active = form.active
        )
        val tagNames = form.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val saved = serviceEntityService.create(entity, tagNames)
        auditService.log(userService.getCurrentUser(), AuditAction.CREATE, AuditEntityType.SERVICE, saved.id, "Created service: ${saved.title}")
        return "redirect:/admin/services"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        val entity = serviceEntityService.findById(id) ?: return "redirect:/admin/services"
        model.addAttribute("form", ServiceForm(
            icon = entity.icon ?: "",
            title = entity.title,
            description = entity.description ?: "",
            locale = entity.locale,
            sortOrder = entity.sortOrder,
            active = entity.active,
            tags = entity.tags.joinToString(", ") { it.name }
        ))
        model.addAttribute("serviceEntity", entity)
        return "admin/services/form"
    }

    @PostMapping("/{id}/edit")
    fun update(@PathVariable id: Long, @Valid @ModelAttribute("form") form: ServiceForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) return "admin/services/form"
        val tagNames = form.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
        serviceEntityService.update(id, form.icon.ifBlank { null }, form.title, form.description.ifBlank { null }, form.locale, form.sortOrder, form.active, tagNames)
        auditService.log(userService.getCurrentUser(), AuditAction.UPDATE, AuditEntityType.SERVICE, id, "Updated service: ${form.title}")
        return "redirect:/admin/services"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        val entity = serviceEntityService.findById(id)
        serviceEntityService.delete(id)
        auditService.log(userService.getCurrentUser(), AuditAction.DELETE, AuditEntityType.SERVICE, id, "Deleted service: ${entity?.title}")
        return "redirect:/admin/services"
    }
}
