package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.ShortCode
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class Base62CodeGenerator : CodeGenerator {

    companion object {
        private const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val BASE = 62
    }

    override fun generate(id: Long): ShortCode {
        require(id > 0) { "ID must be positive: $id" }
        val sb = StringBuilder()
        var remaining = id
        while (remaining > 0) {
            sb.append(ALPHABET[(remaining % BASE).toInt()])
            remaining /= BASE
        }
        return ShortCode(sb.reverse().toString())
    }

    override fun tempCode(): ShortCode =
        ShortCode("_${UUID.randomUUID().toString().take(8)}")
}
