package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.controller.dto.*
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.repository.ClickRepository
import com.ddingmin.jjalb.repository.LinkRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class StatsService(
    private val linkRepository: LinkRepository,
    private val clickRepository: ClickRepository
) {

    suspend fun getStats(code: ShortCode): StatsResponse {
        val link = linkRepository.findByCode(code.value)
            ?: throw NoSuchElementException("Short link not found: ${code.value}")
        val linkId = link.id!!

        return StatsResponse(
            code = link.code!!,
            originalUrl = link.originalUrl,
            author = link.author,
            totalClicks = clickRepository.countByLinkId(linkId),
            dailyClicks = clickRepository.findDailyClicks(linkId)
                .map { DailyClick(it.date, it.count) }.toList(),
            topReferrers = clickRepository.findTopReferrers(linkId)
                .map { ReferrerCount(it.referrer, it.count) }.toList(),
            topUserAgents = clickRepository.findTopUserAgents(linkId)
                .map { UserAgentCount(it.userAgent, it.count) }.toList()
        )
    }
}
