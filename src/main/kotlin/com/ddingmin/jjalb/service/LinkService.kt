package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.ClickEvent
import com.ddingmin.jjalb.domain.Link
import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.repository.LinkRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class LinkService(
    private val linkRepository: LinkRepository,
    private val codeGenerator: CodeGenerator,
    private val linkCacheRepository: LinkCacheRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    suspend fun shorten(originalUrl: OriginalUrl, author: String? = null): Link {
        var link = linkRepository.save(Link.createPending(originalUrl, author))

        val generatedCode = codeGenerator.generate(link.id!!)
        link = linkRepository.save(link.assignCode(generatedCode))

        linkCacheRepository.put(link.shortCode, link.original)
        return link
    }

    suspend fun redirect(code: ShortCode, referrer: String?, userAgent: String?): OriginalUrl {
        val originalUrl = resolve(code)
            ?: throw NoSuchElementException("Short link not found: ${code.value}")

        eventPublisher.publishEvent(ClickEvent(code.value, referrer, userAgent))
        return originalUrl
    }

    suspend fun resolve(code: ShortCode): OriginalUrl? {
        linkCacheRepository.get(code)?.let { return it }

        val link = linkRepository.findByCode(code.value) ?: return null
        linkCacheRepository.put(link.shortCode, link.original)
        return link.original
    }
}
