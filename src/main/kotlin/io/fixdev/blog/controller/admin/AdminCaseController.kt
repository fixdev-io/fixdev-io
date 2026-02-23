package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.dto.CaseStudyForm
import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.model.entity.CaseStudy
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.CaseStudyService
import io.fixdev.blog.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/cases")
class AdminCaseController(
    private val caseStudyService: CaseStudyService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("cases", caseStudyService.findAll())
        return "admin/cases/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("form", CaseStudyForm())
        return "admin/cases/form"
    }

    @PostMapping("/new")
    fun create(@Valid @ModelAttribute("form") form: CaseStudyForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) return "admin/cases/form"
        val entity = CaseStudy(
            industry = form.industry.ifBlank { null },
            title = form.title,
            description = form.description.ifBlank { null },
            locale = form.locale,
            sortOrder = form.sortOrder,
            active = form.active
        )
        val saved = caseStudyService.create(entity, form.metricValues, form.metricLabels)
        auditService.log(userService.getCurrentUser(), AuditAction.CREATE, AuditEntityType.CASE, saved.id, "Created case: ${saved.title}")
        return "redirect:/admin/cases"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        val entity = caseStudyService.findById(id) ?: return "redirect:/admin/cases"
        model.addAttribute("form", CaseStudyForm(
            industry = entity.industry ?: "",
            title = entity.title,
            description = entity.description ?: "",
            locale = entity.locale,
            sortOrder = entity.sortOrder,
            active = entity.active,
            metricValues = entity.metrics.map { it.value },
            metricLabels = entity.metrics.map { it.label }
        ))
        model.addAttribute("caseStudy", entity)
        return "admin/cases/form"
    }

    @PostMapping("/{id}/edit")
    fun update(@PathVariable id: Long, @Valid @ModelAttribute("form") form: CaseStudyForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) return "admin/cases/form"
        caseStudyService.update(id, form.industry.ifBlank { null }, form.title, form.description.ifBlank { null }, form.locale, form.sortOrder, form.active, form.metricValues, form.metricLabels)
        auditService.log(userService.getCurrentUser(), AuditAction.UPDATE, AuditEntityType.CASE, id, "Updated case: ${form.title}")
        return "redirect:/admin/cases"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        val entity = caseStudyService.findById(id)
        caseStudyService.delete(id)
        auditService.log(userService.getCurrentUser(), AuditAction.DELETE, AuditEntityType.CASE, id, "Deleted case: ${entity?.title}")
        return "redirect:/admin/cases"
    }
}
