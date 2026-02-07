package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.service.LinkService
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class RedirectController(
    private val linkService: LinkService
) {

    @GetMapping("/{code}")
    suspend fun redirect(
        @PathVariable code: String,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
        val originalUrl = linkService.redirect(
            code = ShortCode(code),
            referrer = request.headers.getFirst("Referer"),
            userAgent = request.headers.getFirst("User-Agent")
        )

        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(originalUrl.value)
    }
}
