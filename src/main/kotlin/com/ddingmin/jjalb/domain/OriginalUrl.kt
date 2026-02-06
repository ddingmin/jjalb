package com.ddingmin.jjalb.domain

data class OriginalUrl(val value: String) {

    init {
        require(value.isNotBlank()) { "URL must not be blank" }
    }

    companion object {
        fun from(raw: String): OriginalUrl {
            val normalized = if (raw.startsWith("http://") || raw.startsWith("https://")) raw
            else "https://$raw"
            return OriginalUrl(normalized)
        }
    }
}
