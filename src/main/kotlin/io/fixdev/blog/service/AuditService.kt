package io.fixdev.blog.service

import io.fixdev.blog.model.entity.AuditAction
import io.fixdev.blog.model.entity.AuditEntityType
import io.fixdev.blog.model.entity.AuditLog
import io.fixdev.blog.model.entity.User
import io.fixdev.blog.repository.AuditLogRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AuditService(private val auditLogRepository: AuditLogRepository) {

    fun log(user: User?, action: AuditAction, entityType: AuditEntityType, entityId: Long, details: String? = null) {
        auditLogRepository.save(
            AuditLog(
                user = user,
                action = action,
                entityType = entityType,
                entityId = entityId,
                details = details
            )
        )
    }

    fun findAll(pageable: Pageable): Page<AuditLog> =
        auditLogRepository.findAll(pageable)
}
