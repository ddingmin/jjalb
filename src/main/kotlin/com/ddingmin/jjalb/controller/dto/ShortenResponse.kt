package com.ddingmin.jjalb.controller.dto

import com.ddingmin.jjalb.domain.Link

data class ShortenResponse(
    val code: String,
    val shortUrl: String,
    val originalUrl: String
) {

    companion object {
        fun from(link: Link, baseUrl: String): ShortenResponse {
            val code = link.shortCode.value
            return ShortenResponse(
                code = code,
                shortUrl = "$baseUrl/$code",
                originalUrl = link.originalUrl
            )
        }
    }
}
