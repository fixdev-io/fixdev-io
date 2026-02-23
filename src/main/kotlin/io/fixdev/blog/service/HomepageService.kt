package io.fixdev.blog.service

import org.springframework.stereotype.Service

@Service
class HomepageService(
    private val pageBlockService: PageBlockService,
    private val serviceEntityService: ServiceEntityService,
    private val caseStudyService: CaseStudyService,
    private val contactLinkService: ContactLinkService,
    private val techTagService: TechTagService
) {
    fun getBlocksMap(page: String, locale: String) = pageBlockService.getBlocksMap(page, locale)
    fun getServices(locale: String) = serviceEntityService.findActiveByLocale(locale)
    fun getCases(locale: String) = caseStudyService.findActiveByLocale(locale)
    fun getContacts(locale: String) = contactLinkService.findActiveByLocale(locale)
    fun getTechTags(locale: String) = techTagService.findByLocale(locale)
}
