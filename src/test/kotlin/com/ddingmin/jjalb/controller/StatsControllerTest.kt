package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.controller.dto.*
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.service.StatsService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate

@WebFluxTest(StatsController::class, GlobalExceptionHandler::class)
class StatsControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var statsService: StatsService

    @Test
    @DisplayName("코드별 통계를 반환한다")
    fun getStatsByCode() {
        val response = StatsResponse(
            code = "abc123",
            originalUrl = "https://google.com",
            author = null,
            totalClicks = 42,
            dailyClicks = listOf(DailyClick(LocalDate.of(2026, 2, 18), 10)),
            topReferrers = listOf(ReferrerCount("https://twitter.com", 20)),
            topUserAgents = listOf(UserAgentCount("Mozilla/5.0", 30))
        )
        coEvery { statsService.getStatsByCode(ShortCode("abc123")) } returns response

        webTestClient.get()
            .uri("/api/stats/code/abc123")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.code").isEqualTo("abc123")
            .jsonPath("$.totalClicks").isEqualTo(42)
            .jsonPath("$.dailyClicks[0].date").isEqualTo("2026-02-18")
            .jsonPath("$.topReferrers[0].referrer").isEqualTo("https://twitter.com")
    }

    @Test
    @DisplayName("존재하지 않는 코드이면 404를 반환한다")
    fun getStatsByCodeNotFound() {
        coEvery { statsService.getStatsByCode(ShortCode("unknown")) } throws
            NoSuchElementException("Short link not found: unknown")

        webTestClient.get()
            .uri("/api/stats/code/unknown")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    @DisplayName("URL별 통계를 반환한다")
    fun getStatsByUrl() {
        val response = LinkListStatsResponse(
            links = listOf(
                LinkStatsItem("abc", "https://google.com", null, 10, LocalDate.of(2026, 2, 18))
            ),
            totalClicks = 10
        )
        coEvery { statsService.getStatsByUrl("https://google.com") } returns response

        webTestClient.get()
            .uri("/api/stats/url?url=https://google.com")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.totalClicks").isEqualTo(10)
            .jsonPath("$.links[0].code").isEqualTo("abc")
    }

    @Test
    @DisplayName("작성자별 통계를 반환한다")
    fun getStatsByAuthor() {
        val response = LinkListStatsResponse(
            links = listOf(
                LinkStatsItem("abc", "https://google.com", "ddingmin", 5, LocalDate.of(2026, 2, 18))
            ),
            totalClicks = 5
        )
        coEvery { statsService.getStatsByAuthor("ddingmin") } returns response

        webTestClient.get()
            .uri("/api/stats/author/ddingmin")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.totalClicks").isEqualTo(5)
            .jsonPath("$.links[0].author").isEqualTo("ddingmin")
    }
}
