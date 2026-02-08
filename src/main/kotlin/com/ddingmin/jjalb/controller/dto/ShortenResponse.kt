package com.ddingmin.jjalb.controller.dto

import com.ddingmin.jjalb.domain.Link
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "URL 단축 응답")
data class ShortenResponse(
    @Schema(description = "단축 코드", example = "aBcDeF")
    val code: String,
    @Schema(description = "단축 URL", example = "http://localhost:8080/aBcDeF")
    val shortUrl: String,
    @Schema(description = "원본 URL", example = "https://example.com/very/long/path")
    val originalUrl: String,
    @Schema(description = "작성자", example = "ddingmin", nullable = true)
    val author: String? = null
) {

    companion object {
        fun from(link: Link, baseUrl: String): ShortenResponse {
            val code = link.shortCode.value
            return ShortenResponse(
                code = code,
                shortUrl = "$baseUrl/$code",
                originalUrl = link.originalUrl,
                author = link.author
            )
        }
    }
}
