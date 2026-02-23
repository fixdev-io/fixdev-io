package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.ContactLink
import org.springframework.data.jpa.repository.JpaRepository

interface ContactLinkRepository : JpaRepository<ContactLink, Long> {
    fun findByActiveAndLocaleOrderBySortOrder(active: Boolean, locale: String): List<ContactLink>
    fun findAllByOrderBySortOrder(): List<ContactLink>
}
