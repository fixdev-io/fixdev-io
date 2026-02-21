package io.fixdev.blog.service

import io.fixdev.blog.model.entity.Tag
import io.fixdev.blog.repository.TagRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class TagServiceTest {
    @Mock lateinit var tagRepository: TagRepository
    @Mock lateinit var slugService: SlugService
    @InjectMocks lateinit var tagService: TagService

    @Test
    fun `create generates slug from name`() {
        whenever(slugService.generate("Kotlin")).thenReturn("kotlin")
        whenever(tagRepository.save(any<Tag>())).thenAnswer { it.arguments[0] }

        val tag = tagService.create("Kotlin")

        assertThat(tag.name).isEqualTo("Kotlin")
        assertThat(tag.slug).isEqualTo("kotlin")
    }
}
