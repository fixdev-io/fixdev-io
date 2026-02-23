package io.fixdev.blog.service

import io.fixdev.blog.model.entity.PageBlock
import io.fixdev.blog.repository.PageBlockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PageBlockService(private val pageBlockRepository: PageBlockRepository) {

    fun getBlocksMap(page: String, locale: String): Map<String, String> =
        pageBlockRepository.findByPageAndLocaleOrderBySortOrder(page, locale)
            .associate { it.key to (it.value ?: "") }

    fun findByPageAndLocale(page: String, locale: String): List<PageBlock> =
        pageBlockRepository.findByPageAndLocaleOrderBySortOrder(page, locale)

    fun findByPageSectionAndLocale(page: String, section: String, locale: String): List<PageBlock> =
        pageBlockRepository.findByPageAndSectionAndLocaleOrderBySortOrder(page, section, locale)

    fun findById(id: Long): PageBlock? = pageBlockRepository.findById(id).orElse(null)

    @Transactional
    fun save(block: PageBlock): PageBlock = pageBlockRepository.save(block)

    @Transactional
    fun saveAll(blocks: List<PageBlock>) = pageBlockRepository.saveAll(blocks)
}
