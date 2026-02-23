package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.PageBlockService
import io.fixdev.blog.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/blocks")
class AdminPageBlockController(
    private val pageBlockService: PageBlockService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun edit(
        @RequestParam(defaultValue = "ru") locale: String,
        model: Model
    ): String {
        val blocks = pageBlockService.findByPageAndLocale("index", locale)
        model.addAttribute("blocks", blocks)
        model.addAttribute("currentLocale", locale)
        return "admin/blocks/edit"
    }

    @PostMapping
    fun save(
        @RequestParam ids: List<Long>,
        @RequestParam values: List<String>,
        @RequestParam(defaultValue = "ru") locale: String
    ): String {
        val currentUser = userService.getCurrentUser()
        ids.zip(values).forEach { (id, value) ->
            val block = pageBlockService.findById(id) ?: return@forEach
            block.value = value
            pageBlockService.save(block)
            auditService.log(currentUser, AuditAction.UPDATE, AuditEntityType.PAGE_BLOCK, id, "Updated block: ${block.key}")
        }
        return "redirect:/admin/blocks?locale=$locale"
    }
}
