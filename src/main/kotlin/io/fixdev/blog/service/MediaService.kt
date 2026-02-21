package io.fixdev.blog.service

import io.fixdev.blog.config.MediaProperties
import io.fixdev.blog.model.entity.Media
import io.fixdev.blog.repository.MediaRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@Service
class MediaService(
    private val mediaRepository: MediaRepository,
    private val mediaProperties: MediaProperties
) {
    fun upload(file: MultipartFile): Media {
        val mimeType = file.contentType ?: throw IllegalArgumentException("Missing content type")
        if (mimeType !in mediaProperties.allowedTypes) {
            throw IllegalArgumentException("File type $mimeType not allowed")
        }
        if (file.size > mediaProperties.maxSizeBytes) {
            throw IllegalArgumentException("File too large")
        }

        val uploadDir = Path.of(mediaProperties.uploadDir)
        Files.createDirectories(uploadDir)

        val storedName = "${UUID.randomUUID()}_${file.originalFilename}"
        val targetPath = uploadDir.resolve(storedName)
        file.transferTo(targetPath)

        return mediaRepository.save(
            Media(
                filename = file.originalFilename ?: storedName,
                storedPath = storedName,
                mimeType = mimeType,
                sizeBytes = file.size
            )
        )
    }

    fun findAll(): List<Media> = mediaRepository.findAll()

    fun findById(id: Long): Media? = mediaRepository.findById(id).orElse(null)

    fun getFilePath(media: Media): Path =
        Path.of(mediaProperties.uploadDir).resolve(media.storedPath)

    fun delete(id: Long) {
        val media = mediaRepository.findById(id).orElseThrow()
        val path = Path.of(mediaProperties.uploadDir).resolve(media.storedPath)
        Files.deleteIfExists(path)
        mediaRepository.deleteById(id)
    }
}
