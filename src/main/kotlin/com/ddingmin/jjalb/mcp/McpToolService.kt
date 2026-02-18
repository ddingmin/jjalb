package com.ddingmin.jjalb.mcp

import com.ddingmin.jjalb.controller.dto.StatsResponse
import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.service.LinkService
import com.ddingmin.jjalb.service.StatsService
import kotlinx.coroutines.runBlocking
import org.springframework.ai.tool.annotation.Tool
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class McpToolService(
    private val linkService: LinkService,
    private val statsService: StatsService,
    @Value("\${app.base-url}")
    private val baseUrl: String
) {

    @Tool(description = "Shorten a URL and return the short link. Takes a URL string and optional author string.")
    fun shortenUrl(url: String, author: String?): Map<String, Any?> = runBlocking {
        val originalUrl = OriginalUrl.from(url)
        val link = linkService.shorten(originalUrl, author)
        val code = link.shortCode.value
        mapOf(
            "code" to code,
            "shortUrl" to "$baseUrl/$code",
            "originalUrl" to link.originalUrl,
            "statsUrl" to "$baseUrl/stats/$code"
        )
    }

    @Tool(description = "Get click statistics for a short code. Returns total clicks, daily clicks, top referrers, and top user agents.")
    fun getStats(code: String): StatsResponse = runBlocking {
        statsService.getStatsByCode(ShortCode(code))
    }
}
