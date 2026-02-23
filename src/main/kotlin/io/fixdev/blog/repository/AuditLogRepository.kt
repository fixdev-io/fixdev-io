package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.model.entity.AuditLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogRepository : JpaRepository<AuditLog, Long> {
    override fun findAll(pageable: Pageable): Page<AuditLog>
    fun findByEntityType(entityType: AuditEntityType, pageable: Pageable): Page<AuditLog>
}
