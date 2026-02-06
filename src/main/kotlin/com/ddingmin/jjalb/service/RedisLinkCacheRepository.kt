package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.domain.ShortCode
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisLinkCacheRepository(
    private val redisTemplate: ReactiveStringRedisTemplate
) : LinkCacheRepository {

    companion object {
        private const val KEY_PREFIX = "link:"
        private val TTL = Duration.ofHours(24)
    }

    override suspend fun get(code: ShortCode): OriginalUrl? {
        val value = redisTemplate.opsForValue()
            .get("$KEY_PREFIX${code.value}")
            .awaitSingleOrNull()
        return value?.let { OriginalUrl(it) }
    }

    override suspend fun put(code: ShortCode, url: OriginalUrl) {
        redisTemplate.opsForValue()
            .set("$KEY_PREFIX${code.value}", url.value, TTL)
            .awaitSingle()
    }
}
