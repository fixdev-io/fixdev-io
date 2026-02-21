package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.dto.ArticleForm
import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.TagService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/articles")
class AdminArticleController(
    private val articleService: ArticleService,
    private val tagService: TagService
) {
    @GetMapping
    fun list(@RequestParam(defaultValue = "0") page: Int, model: Model): String {
        val pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending())
        model.addAttribute("articles", articleService.findAll(pageable))
        return "admin/articles/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("form", ArticleForm())
        model.addAttribute("tags", tagService.findAll())
        return "admin/articles/form"
    }

    @PostMapping("/new")
    fun create(@Valid @ModelAttribute("form") form: ArticleForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) {
            model.addAttribute("tags", tagService.findAll())
            return "admin/articles/form"
        }
        val article = articleService.create(form)
        return "redirect:/admin/articles/${article.id}/edit"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        val article = articleService.findById(id) ?: return "redirect:/admin/articles"
        model.addAttribute("form", ArticleForm(
            title = article.title,
            content = article.content ?: "",
            excerpt = article.excerpt ?: "",
            coverImageUrl = article.coverImageUrl ?: "",
            seoTitle = article.seoTitle ?: "",
            seoDescription = article.seoDescription ?: "",
            tagIds = article.tags.map { it.id },
            publish = false
        ))
        model.addAttribute("article", article)
        model.addAttribute("tags", tagService.findAll())
        return "admin/articles/form"
    }

    @PostMapping("/{id}/edit")
    fun update(@PathVariable id: Long, @Valid @ModelAttribute("form") form: ArticleForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) {
            model.addAttribute("tags", tagService.findAll())
            return "admin/articles/form"
        }
        articleService.update(id, form)
        return "redirect:/admin/articles/${id}/edit"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        articleService.delete(id)
        return "redirect:/admin/articles"
    }
}
