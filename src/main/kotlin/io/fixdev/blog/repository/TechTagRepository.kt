package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.TechTag
import org.springframework.data.jpa.repository.JpaRepository

interface TechTagRepository : JpaRepository<TechTag, Long> {
    fun findByLocaleOrderBySortOrder(locale: String): List<TechTag>
    fun findAllByOrderBySortOrder(): List<TechTag>
}
