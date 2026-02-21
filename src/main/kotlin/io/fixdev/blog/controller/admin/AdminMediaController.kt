package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.MediaService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/admin/media")
class AdminMediaController(private val mediaService: MediaService) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("media", mediaService.findAll())
        return "admin/media/list"
    }

    @PostMapping("/upload")
    @ResponseBody
    fun upload(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val media = mediaService.upload(file)
        return mapOf("location" to "/media/${media.id}")
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        mediaService.delete(id)
        return "redirect:/admin/media"
    }
}
