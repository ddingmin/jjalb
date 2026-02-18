package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.controller.dto.StatsResponse
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.service.StatsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "통계", description = "클릭 통계 조회 API")
@RestController
@RequestMapping("/api")
class StatsController(
    private val statsService: StatsService
) {

    @Operation(summary = "클릭 통계 조회", description = "단축 코드에 대한 클릭 통계를 조회합니다.")
    @GetMapping("/stats/{code}")
    suspend fun getStats(@PathVariable code: String): StatsResponse =
        statsService.getStats(ShortCode(code))
}
