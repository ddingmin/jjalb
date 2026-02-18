package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.controller.dto.LinkListStatsResponse
import com.ddingmin.jjalb.controller.dto.StatsResponse
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.service.StatsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "통계", description = "클릭 통계 조회 API")
@RestController
@RequestMapping("/api/stats")
class StatsController(
    private val statsService: StatsService
) {

    @Operation(summary = "코드별 클릭 통계", description = "단축 코드에 대한 클릭 통계를 조회합니다.")
    @GetMapping("/code/{code}")
    suspend fun getStatsByCode(@PathVariable code: String): StatsResponse =
        statsService.getStatsByCode(ShortCode(code))

    @Operation(summary = "URL별 클릭 통계", description = "원본 URL에 대한 모든 단축 링크의 통계를 조회합니다.")
    @GetMapping("/url")
    suspend fun getStatsByUrl(@RequestParam url: String): LinkListStatsResponse =
        statsService.getStatsByUrl(url)

    @Operation(summary = "작성자별 클릭 통계", description = "작성자의 모든 단축 링크 통계를 조회합니다.")
    @GetMapping("/author/{author}")
    suspend fun getStatsByAuthor(@PathVariable author: String): LinkListStatsResponse =
        statsService.getStatsByAuthor(author)
}
