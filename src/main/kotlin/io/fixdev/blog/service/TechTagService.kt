package io.fixdev.blog.service

import io.fixdev.blog.model.entity.TechTag
import io.fixdev.blog.repository.TechTagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TechTagService(private val techTagRepository: TechTagRepository) {

    fun findByLocale(locale: String): List<TechTag> =
        techTagRepository.findByLocaleOrderBySortOrder(locale)

    fun findAll(): List<TechTag> = techTagRepository.findAllByOrderBySortOrder()

    fun findById(id: Long): TechTag? = techTagRepository.findById(id).orElse(null)

    @Transactional
    fun save(tag: TechTag): TechTag = techTagRepository.save(tag)

    fun delete(id: Long) = techTagRepository.deleteById(id)
}
