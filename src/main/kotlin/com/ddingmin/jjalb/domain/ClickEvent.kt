package com.ddingmin.jjalb.domain

data class ClickEvent(
    val code: String,
    val referrer: String?,
    val userAgent: String?
)
