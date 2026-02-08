package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.service.LinkService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@Tag(name = "리다이렉트", description = "단축 링크 리다이렉트 API")
@RestController
class RedirectController(
    private val linkService: LinkService
) {

    @Operation(summary = "단축 링크 리다이렉트", description = "단축 코드로 접속하면 원본 URL로 302 리다이렉트합니다.")
    @ApiResponses(
        ApiResponse(
            responseCode = "302", description = "원본 URL로 리다이렉트",
            headers = [Header(name = "Location", description = "원본 URL", schema = Schema(type = "string"))]
        ),
        ApiResponse(
            responseCode = "400", description = "유효하지 않은 단축 코드",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "단축 링크를 찾을 수 없음",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @GetMapping("/{code}")
    suspend fun redirect(
        @Parameter(description = "단축 코드", example = "aBcDeF")
        @PathVariable
        code: String,
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
