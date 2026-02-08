package com.ddingmin.jjalb.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "URL 단축 요청")
data class ShortenRequest(
    @field:NotBlank
    @Schema(description = "단축할 원본 URL", example = "https://example.com/very/long/path")
    val url: String,
    @Schema(description = "작성자 (선택)", example = "ddingmin", nullable = true)
    val author: String? = null
)
