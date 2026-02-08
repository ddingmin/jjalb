package com.ddingmin.jjalb.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("link")
data class Link(
    @Id
    val id: Long? = null,
    val code: String? = null,
    val originalUrl: String,
    val author: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    val shortCode: ShortCode
        get() = ShortCode(code ?: throw IllegalStateException("Code not yet assigned"))

    val original: OriginalUrl
        get() = OriginalUrl(originalUrl)

    fun assignCode(shortCode: ShortCode): Link =
        copy(code = shortCode.value)

    companion object {
        fun createPending(originalUrl: OriginalUrl, author: String? = null): Link =
            Link(originalUrl = originalUrl.value, author = author)

        fun create(code: ShortCode, originalUrl: OriginalUrl, author: String? = null): Link =
            Link(code = code.value, originalUrl = originalUrl.value, author = author)
    }
}
