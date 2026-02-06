package com.ddingmin.jjalb.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("link")
data class Link(
    @Id
    val id: Long? = null,
    val code: String,
    val originalUrl: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    val shortCode: ShortCode
        get() = ShortCode(code)

    val original: OriginalUrl
        get() = OriginalUrl(originalUrl)

    fun assignCode(shortCode: ShortCode): Link =
        copy(code = shortCode.value)

    companion object {
        fun create(code: ShortCode, originalUrl: OriginalUrl): Link =
            Link(code = code.value, originalUrl = originalUrl.value)
    }
}
