package com.ddingmin.jjalb.domain

data class ShortCode(val value: String) {

    init {
        require(value.isNotBlank()) { "Short code must not be blank" }
        require(value.length <= MAX_LENGTH) { "Short code must be at most $MAX_LENGTH characters: $value" }
        require(PATTERN.matches(value)) { "Short code contains invalid characters: $value" }
    }

    companion object {
        private const val MAX_LENGTH = 10
        private val PATTERN = Regex("^[0-9a-zA-Z]+$")
    }
}
