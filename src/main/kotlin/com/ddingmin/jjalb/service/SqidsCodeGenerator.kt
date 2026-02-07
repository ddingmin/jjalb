package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.ShortCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.sqids.Sqids

@Component
class SqidsCodeGenerator(
    @Value("\${app.sqids.alphabet}") alphabet: String,
    @Value("\${app.sqids.min-length}") minLength: Int
) : CodeGenerator {

    private val sqids: Sqids = Sqids.builder()
        .alphabet(alphabet)
        .minLength(minLength)
        .build()

    override fun generate(id: Long): ShortCode {
        require(id > 0) { "ID must be positive: $id" }
        return ShortCode(sqids.encode(listOf(id)))
    }
}
