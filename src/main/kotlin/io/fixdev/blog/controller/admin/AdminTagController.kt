package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.TagService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/tags")
class AdminTagController(private val tagService: TagService) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("tags", tagService.findAll())
        return "admin/tags/list"
    }

    @PostMapping
    fun create(@RequestParam name: String): String {
        tagService.create(name)
        return "redirect:/admin/tags"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        tagService.delete(id)
        return "redirect:/admin/tags"
    }
}
