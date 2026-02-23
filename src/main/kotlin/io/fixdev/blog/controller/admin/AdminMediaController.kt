package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.service.AuditService
import io.fixdev.blog.service.MediaService
import io.fixdev.blog.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/admin/media")
class AdminMediaController(
    private val mediaService: MediaService,
    private val auditService: AuditService,
    private val userService: UserService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("media", mediaService.findAll())
        return "admin/media/list"
    }

    @PostMapping("/upload")
    @ResponseBody
    fun upload(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val media = mediaService.upload(file)
        auditService.log(userService.getCurrentUser(), AuditAction.UPLOAD, AuditEntityType.MEDIA, media.id, "Uploaded: ${media.filename}")
        return mapOf("location" to "/media/${media.id}")
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        val media = mediaService.findById(id)
        mediaService.delete(id)
        auditService.log(userService.getCurrentUser(), AuditAction.DELETE, AuditEntityType.MEDIA, id, "Deleted: ${media?.filename}")
        return "redirect:/admin/media"
    }
}
