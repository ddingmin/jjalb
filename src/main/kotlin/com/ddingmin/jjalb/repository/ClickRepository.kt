package com.ddingmin.jjalb.repository

import com.ddingmin.jjalb.domain.Click
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ClickRepository : CoroutineCrudRepository<Click, Long> {

    suspend fun countByLinkId(linkId: Long): Long

    @Query("""
        SELECT DATE(clicked_at) AS date, COUNT(*) AS count
        FROM click WHERE link_id = :linkId
        GROUP BY DATE(clicked_at) ORDER BY date DESC LIMIT 30
    """)
    fun findDailyClicks(linkId: Long): Flow<DailyClickRow>

    @Query("""
        SELECT referrer, COUNT(*) AS count
        FROM click WHERE link_id = :linkId AND referrer IS NOT NULL
        GROUP BY referrer ORDER BY count DESC LIMIT 10
    """)
    fun findTopReferrers(linkId: Long): Flow<ReferrerRow>

    @Query("""
        SELECT user_agent AS "userAgent", COUNT(*) AS count
        FROM click WHERE link_id = :linkId AND user_agent IS NOT NULL
        GROUP BY user_agent ORDER BY count DESC LIMIT 10
    """)
    fun findTopUserAgents(linkId: Long): Flow<UserAgentRow>
}

data class DailyClickRow(val date: java.time.LocalDate, val count: Long)
data class ReferrerRow(val referrer: String, val count: Long)
data class UserAgentRow(val userAgent: String, val count: Long)
