package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.domain.Link
import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.service.LinkService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(ShortenController::class, GlobalExceptionHandler::class)
class ShortenControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var linkService: LinkService

    @Test
    @DisplayName("유효한 URL이면 201과 단축 링크를 반환한다")
    fun shortenSuccess() {
        val link = Link(id = 1L, code = "Xr04lY", originalUrl = "https://google.com")
        coEvery { linkService.shorten(OriginalUrl("https://google.com")) } returns link

        webTestClient.post()
            .uri("/api/shorten")
            .header("Content-Type", "application/json")
            .bodyValue("""{"url": "https://google.com"}""")
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.code").isEqualTo("Xr04lY")
            .jsonPath("$.originalUrl").isEqualTo("https://google.com")
            .jsonPath("$.shortUrl").exists()
            .jsonPath("$.statsUrl").exists()
    }

    @Test
    @DisplayName("빈 URL이면 400을 반환한다")
    fun shortenEmptyUrl() {
        webTestClient.post()
            .uri("/api/shorten")
            .header("Content-Type", "application/json")
            .bodyValue("""{"url": ""}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    @DisplayName("body가 없으면 400을 반환한다")
    fun shortenNoBody() {
        webTestClient.post()
            .uri("/api/shorten")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    @DisplayName("위험한 URL이면 400을 반환한다")
    fun shortenDangerousUrl() {
        webTestClient.post()
            .uri("/api/shorten")
            .header("Content-Type", "application/json")
            .bodyValue("""{"url": "https://127.0.0.1/admin"}""")
            .exchange()
            .expectStatus().isBadRequest
    }
}
