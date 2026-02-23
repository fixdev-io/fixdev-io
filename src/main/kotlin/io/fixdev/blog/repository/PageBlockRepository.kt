package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.PageBlock
import org.springframework.data.jpa.repository.JpaRepository

interface PageBlockRepository : JpaRepository<PageBlock, Long> {
    fun findByPageAndLocaleOrderBySortOrder(page: String, locale: String): List<PageBlock>
    fun findByPageAndSectionAndLocaleOrderBySortOrder(page: String, section: String, locale: String): List<PageBlock>
}
