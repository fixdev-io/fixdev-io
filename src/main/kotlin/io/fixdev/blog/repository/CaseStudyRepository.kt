package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.CaseStudy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CaseStudyRepository : JpaRepository<CaseStudy, Long> {
    @Query("SELECT DISTINCT c FROM CaseStudy c LEFT JOIN FETCH c.metrics WHERE c.active = :active AND c.locale = :locale ORDER BY c.sortOrder")
    fun findByActiveAndLocaleOrderBySortOrder(active: Boolean, locale: String): List<CaseStudy>

    @Query("SELECT DISTINCT c FROM CaseStudy c LEFT JOIN FETCH c.metrics ORDER BY c.sortOrder")
    fun findAllByOrderBySortOrder(): List<CaseStudy>
}
