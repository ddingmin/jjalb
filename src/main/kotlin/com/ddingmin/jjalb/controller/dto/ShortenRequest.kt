package com.ddingmin.jjalb.controller.dto

import jakarta.validation.constraints.NotBlank

data class ShortenRequest(
    @field:NotBlank
    val url: String
)
