package com.ddingmin.jjalb.controller

import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.service.LinkService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(RedirectController::class, GlobalExceptionHandler::class)
class RedirectControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var linkService: LinkService

    @Test
    @DisplayName("유효한 코드이면 302와 Location 헤더를 반환한다")
    fun redirectSuccess() {
        coEvery {
            linkService.redirect(ShortCode("abc123"), any(), any())
        } returns OriginalUrl("https://google.com")

        webTestClient.get()
            .uri("/abc123")
            .exchange()
            .expectStatus().isFound
            .expectHeader().location("https://google.com")
    }

    @Test
    @DisplayName("존재하지 않는 코드이면 404를 반환한다")
    fun redirectNotFound() {
        coEvery {
            linkService.redirect(ShortCode("unknown1"), any(), any())
        } throws NoSuchElementException("Short link not found: unknown1")

        webTestClient.get()
            .uri("/unknown1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    @DisplayName("잘못된 형식의 코드이면 400을 반환한다")
    fun redirectInvalidCode() {
        webTestClient.get()
            .uri("/abc-123")
            .exchange()
            .expectStatus().isBadRequest
    }
}
