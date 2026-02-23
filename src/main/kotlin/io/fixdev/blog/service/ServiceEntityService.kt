package io.fixdev.blog.service

import io.fixdev.blog.model.entity.ServiceEntity
import io.fixdev.blog.model.entity.ServiceTag
import io.fixdev.blog.repository.ServiceEntityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ServiceEntityService(private val serviceEntityRepository: ServiceEntityRepository) {

    fun findActiveByLocale(locale: String): List<ServiceEntity> =
        serviceEntityRepository.findByActiveAndLocaleOrderBySortOrder(true, locale)

    fun findAll(): List<ServiceEntity> = serviceEntityRepository.findAllByOrderBySortOrder()

    fun findById(id: Long): ServiceEntity? = serviceEntityRepository.findById(id).orElse(null)

    @Transactional
    fun create(entity: ServiceEntity, tagNames: List<String>): ServiceEntity {
        val saved = serviceEntityRepository.save(entity)
        tagNames.forEachIndexed { i, name ->
            saved.tags.add(ServiceTag(service = saved, name = name, sortOrder = i))
        }
        return serviceEntityRepository.save(saved)
    }

    @Transactional
    fun update(id: Long, icon: String?, title: String, description: String?, locale: String, sortOrder: Int, active: Boolean, tagNames: List<String>): ServiceEntity {
        val entity = serviceEntityRepository.findById(id).orElseThrow()
        entity.icon = icon
        entity.title = title
        entity.description = description
        entity.locale = locale
        entity.sortOrder = sortOrder
        entity.active = active
        entity.tags.clear()
        tagNames.forEachIndexed { i, name ->
            entity.tags.add(ServiceTag(service = entity, name = name, sortOrder = i))
        }
        return serviceEntityRepository.save(entity)
    }

    fun delete(id: Long) = serviceEntityRepository.deleteById(id)
}
