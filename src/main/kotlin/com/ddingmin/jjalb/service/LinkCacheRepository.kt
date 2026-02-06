package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.domain.ShortCode

interface LinkCacheRepository {
    suspend fun get(code: ShortCode): OriginalUrl?
    suspend fun put(code: ShortCode, url: OriginalUrl)
}
