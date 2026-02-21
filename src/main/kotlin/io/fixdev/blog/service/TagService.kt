package io.fixdev.blog.service

import io.fixdev.blog.model.entity.Tag
import io.fixdev.blog.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val slugService: SlugService
) {
    fun findAll(): List<Tag> = tagRepository.findAll()
    fun findBySlug(slug: String): Tag? = tagRepository.findBySlug(slug).orElse(null)
    fun findById(id: Long): Tag? = tagRepository.findById(id).orElse(null)

    @Transactional
    fun create(name: String): Tag =
        tagRepository.save(Tag(name = name, slug = slugService.generate(name)))

    @Transactional
    fun update(id: Long, name: String): Tag {
        val tag = tagRepository.findById(id).orElseThrow()
        tag.name = name
        tag.slug = slugService.generate(name)
        return tagRepository.save(tag)
    }

    fun delete(id: Long) = tagRepository.deleteById(id)
}
