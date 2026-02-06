package com.ddingmin.jjalb.domain

data class ShortCode(val value: String) {

    init {
        require(value.isNotBlank()) { "Short code must not be blank" }
    }

    val isTemporary: Boolean
        get() = value.startsWith("_")
}
