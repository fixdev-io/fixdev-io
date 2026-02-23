package io.fixdev.blog.service

import io.fixdev.blog.model.entity.ContactLink
import io.fixdev.blog.repository.ContactLinkRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContactLinkService(private val contactLinkRepository: ContactLinkRepository) {

    fun findActiveByLocale(locale: String): List<ContactLink> =
        contactLinkRepository.findByActiveAndLocaleOrderBySortOrder(true, locale)

    fun findAll(): List<ContactLink> = contactLinkRepository.findAllByOrderBySortOrder()

    fun findById(id: Long): ContactLink? = contactLinkRepository.findById(id).orElse(null)

    @Transactional
    fun save(link: ContactLink): ContactLink = contactLinkRepository.save(link)

    fun delete(id: Long) = contactLinkRepository.deleteById(id)
}
