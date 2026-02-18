package com.ddingmin.jjalb.controller.dto

import java.time.LocalDate

data class StatsResponse(
    val code: String,
    val originalUrl: String,
    val author: String?,
    val totalClicks: Long,
    val dailyClicks: List<DailyClick>,
    val topReferrers: List<ReferrerCount>,
    val topUserAgents: List<UserAgentCount>
)

data class DailyClick(val date: LocalDate, val count: Long)
data class ReferrerCount(val referrer: String, val count: Long)
data class UserAgentCount(val userAgent: String, val count: Long)
