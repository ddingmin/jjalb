package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.controller.dto.ShortenRequest
import com.ddingmin.jjalb.controller.dto.ShortenResponse
import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.service.LinkService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ShortenController(
    private val linkService: LinkService,
    @Value("\${app.base-url}") private val baseUrl: String
) {

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun shorten(@Valid @RequestBody request: ShortenRequest): ShortenResponse {
        val originalUrl = OriginalUrl.from(request.url)
        val link = linkService.shorten(originalUrl)
        return ShortenResponse.from(link, baseUrl)
    }
}
