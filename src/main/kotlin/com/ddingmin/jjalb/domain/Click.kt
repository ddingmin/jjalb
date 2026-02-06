package com.ddingmin.jjalb.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("click")
data class Click(
    @Id
    val id: Long? = null,
    val linkId: Long,
    val clickedAt: LocalDateTime = LocalDateTime.now(),
    val referrer: String? = null,
    val userAgent: String? = null
) {

    companion object {
        fun of(link: Link, referrer: String?, userAgent: String?): Click {
            requireNotNull(link.id) { "Link must be persisted before recording a click" }
            return Click(
                linkId = link.id,
                referrer = referrer,
                userAgent = userAgent
            )
        }
    }
}
