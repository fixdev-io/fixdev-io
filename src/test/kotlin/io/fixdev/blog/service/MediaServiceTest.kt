package io.fixdev.blog.service

import io.fixdev.blog.config.MediaProperties
import io.fixdev.blog.repository.MediaRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Path

@ExtendWith(MockitoExtension::class)
class MediaServiceTest {
    @Mock lateinit var mediaRepository: MediaRepository

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `upload rejects non-image file`() {
        val props = MediaProperties(uploadDir = tempDir.toString())
        val service = MediaService(mediaRepository, props)
        val file = MockMultipartFile("file", "doc.pdf", "application/pdf", "data".toByteArray())

        assertThatThrownBy { service.upload(file) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("not allowed")
    }

    @Test
    fun `upload saves image to disk and database`() {
        val props = MediaProperties(uploadDir = tempDir.toString())
        val service = MediaService(mediaRepository, props)
        val file = MockMultipartFile("file", "photo.jpg", "image/jpeg", "imagedata".toByteArray())
        whenever(mediaRepository.save(any())).thenAnswer { it.arguments[0] }

        val media = service.upload(file)

        assertThat(media.filename).isEqualTo("photo.jpg")
        assertThat(media.mimeType).isEqualTo("image/jpeg")
    }
}
