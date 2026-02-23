package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.AuditService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/audit")
class AdminAuditController(private val auditService: AuditService) {

    @GetMapping
    fun list(@RequestParam(defaultValue = "0") page: Int, model: Model): String {
        val pageable = PageRequest.of(page, 50, Sort.by("createdAt").descending())
        model.addAttribute("logs", auditService.findAll(pageable))
        return "admin/audit/list"
    }
}
