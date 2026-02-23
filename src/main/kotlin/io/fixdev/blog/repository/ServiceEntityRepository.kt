package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.ServiceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ServiceEntityRepository : JpaRepository<ServiceEntity, Long> {
    @Query("SELECT DISTINCT s FROM ServiceEntity s LEFT JOIN FETCH s.tags WHERE s.active = :active AND s.locale = :locale ORDER BY s.sortOrder")
    fun findByActiveAndLocaleOrderBySortOrder(active: Boolean, locale: String): List<ServiceEntity>

    fun findByLocaleOrderBySortOrder(locale: String): List<ServiceEntity>

    @Query("SELECT DISTINCT s FROM ServiceEntity s LEFT JOIN FETCH s.tags ORDER BY s.sortOrder")
    fun findAllByOrderBySortOrder(): List<ServiceEntity>
}
