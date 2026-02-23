package io.fixdev.blog.service

import io.fixdev.blog.model.entity.CaseMetric
import io.fixdev.blog.model.entity.CaseStudy
import io.fixdev.blog.repository.CaseStudyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CaseStudyService(private val caseStudyRepository: CaseStudyRepository) {

    fun findActiveByLocale(locale: String): List<CaseStudy> =
        caseStudyRepository.findByActiveAndLocaleOrderBySortOrder(true, locale)

    fun findAll(): List<CaseStudy> = caseStudyRepository.findAllByOrderBySortOrder()

    fun findById(id: Long): CaseStudy? = caseStudyRepository.findById(id).orElse(null)

    @Transactional
    fun create(entity: CaseStudy, metricValues: List<String>, metricLabels: List<String>): CaseStudy {
        val saved = caseStudyRepository.save(entity)
        metricValues.zip(metricLabels).forEachIndexed { i, (v, l) ->
            saved.metrics.add(CaseMetric(caseStudy = saved, value = v, label = l, sortOrder = i))
        }
        return caseStudyRepository.save(saved)
    }

    @Transactional
    fun update(id: Long, industry: String?, title: String, description: String?, locale: String, sortOrder: Int, active: Boolean, metricValues: List<String>, metricLabels: List<String>): CaseStudy {
        val entity = caseStudyRepository.findById(id).orElseThrow()
        entity.industry = industry
        entity.title = title
        entity.description = description
        entity.locale = locale
        entity.sortOrder = sortOrder
        entity.active = active
        entity.metrics.clear()
        metricValues.zip(metricLabels).forEachIndexed { i, (v, l) ->
            entity.metrics.add(CaseMetric(caseStudy = entity, value = v, label = l, sortOrder = i))
        }
        return caseStudyRepository.save(entity)
    }

    fun delete(id: Long) = caseStudyRepository.deleteById(id)
}
