package com.ddingmin.jjalb.controller.dto

import com.ddingmin.jjalb.domain.Link

data class ShortenResponse(
    val code: String,
    val shortUrl: String,
    val originalUrl: String
) {

    companion object {
        fun from(link: Link, baseUrl: String): ShortenResponse =
            ShortenResponse(
                code = link.code,
                shortUrl = "$baseUrl/${link.code}",
                originalUrl = link.originalUrl
            )
    }
}
