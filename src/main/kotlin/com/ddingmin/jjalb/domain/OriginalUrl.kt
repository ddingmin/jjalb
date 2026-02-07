package com.ddingmin.jjalb.domain

import java.net.InetAddress
import java.net.URI

data class OriginalUrl(val value: String) {

    init {
        require(value.isNotBlank()) { "URL must not be blank" }
        require(value.length <= MAX_LENGTH) { "URL must be at most $MAX_LENGTH characters" }
        require(ALLOWED_SCHEMES.any { value.lowercase().startsWith(it) }) {
            "Only http/https URLs are allowed"
        }

        val uri = runCatching { URI(value) }.getOrElse {
            throw IllegalArgumentException("Invalid URL format: $value")
        }
        val host = uri.host
        require(!host.isNullOrBlank()) { "URL must have a valid host" }
        require(!isPrivateAddress(host)) { "Private/internal URLs are not allowed" }
    }

    companion object {
        private const val MAX_LENGTH = 2048
        private val ALLOWED_SCHEMES = listOf("http://", "https://")

        private val PRIVATE_HOSTS = setOf("localhost", "0.0.0.0", "[::]", "[::1]")

        private fun isPrivateAddress(host: String): Boolean {
            if (host.lowercase() in PRIVATE_HOSTS) return true

            return runCatching {
                val address = InetAddress.getByName(host)
                address.isLoopbackAddress ||
                    address.isLinkLocalAddress ||
                    address.isSiteLocalAddress ||
                    address.isAnyLocalAddress
            }.getOrDefault(false)
        }

        fun from(raw: String): OriginalUrl {
            val normalized = if (raw.lowercase().startsWith("http://") ||
                raw.lowercase().startsWith("https://")
            ) raw
            else "https://$raw"
            return OriginalUrl(normalized)
        }
    }
}
