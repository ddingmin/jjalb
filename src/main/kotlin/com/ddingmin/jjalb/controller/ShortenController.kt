package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.controller.dto.ShortenRequest
import com.ddingmin.jjalb.controller.dto.ShortenResponse
import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.service.LinkService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "URL 단축", description = "URL 단축 링크 생성 API")
@RestController
@RequestMapping("/api")
class ShortenController(
    private val linkService: LinkService,
    @Value("\${app.base-url}")
    private val baseUrl: String
) {

    @Operation(summary = "단축 링크 생성", description = "원본 URL을 받아 단축 링크를 생성합니다.")
    @ApiResponses(
        ApiResponse(
            responseCode = "201", description = "단축 링크 생성 성공",
            content = [Content(schema = Schema(implementation = ShortenResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "잘못된 요청 (URL 누락, 유효하지 않은 URL 등)",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun shorten(
        @Valid
        @RequestBody
        request: ShortenRequest
    ): ShortenResponse {
        val originalUrl = OriginalUrl.from(request.url)
        val link = linkService.shorten(originalUrl, request.author)
        return ShortenResponse.from(link, baseUrl)
    }
}
