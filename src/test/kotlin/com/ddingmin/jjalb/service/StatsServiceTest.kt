package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.Link
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.repository.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class StatsServiceTest : DescribeSpec({

    val linkRepository = mockk<LinkRepository>()
    val clickRepository = mockk<ClickRepository>()
    val statsService = StatsService(linkRepository, clickRepository)

    beforeEach { clearAllMocks() }

    describe("getStatsByCode") {
        it("존재하는 코드에 대해 통계를 반환한다") {
            val link = Link(id = 1L, code = "abc123", originalUrl = "https://google.com", author = "tester")
            coEvery { linkRepository.findByCode("abc123") } returns link
            coEvery { clickRepository.countByLinkId(1L) } returns 42L
            coEvery { clickRepository.findDailyClicks(1L) } returns flowOf(
                DailyClickRow(LocalDate.of(2026, 2, 18), 10L)
            )
            coEvery { clickRepository.findTopReferrers(1L) } returns flowOf(
                ReferrerRow("https://twitter.com", 20L)
            )
            coEvery { clickRepository.findTopUserAgents(1L) } returns flowOf(
                UserAgentRow("Mozilla/5.0", 30L)
            )

            val result = statsService.getStatsByCode(ShortCode("abc123"))

            result.code shouldBe "abc123"
            result.originalUrl shouldBe "https://google.com"
            result.author shouldBe "tester"
            result.totalClicks shouldBe 42L
            result.dailyClicks.size shouldBe 1
            result.dailyClicks[0].date shouldBe LocalDate.of(2026, 2, 18)
            result.topReferrers.size shouldBe 1
            result.topReferrers[0].referrer shouldBe "https://twitter.com"
            result.topUserAgents.size shouldBe 1
            result.topUserAgents[0].userAgent shouldBe "Mozilla/5.0"
        }

        it("존재하지 않는 코드이면 NoSuchElementException을 던진다") {
            coEvery { linkRepository.findByCode("unknown") } returns null

            shouldThrow<NoSuchElementException> {
                statsService.getStatsByCode(ShortCode("unknown"))
            }
        }
    }

    describe("getStatsByUrl") {
        it("URL에 대한 링크 목록 통계를 반환한다") {
            val links = listOf(
                Link(id = 1L, code = "abc", originalUrl = "https://google.com", author = "a"),
                Link(id = 2L, code = "def", originalUrl = "https://google.com", author = "b")
            )
            coEvery { linkRepository.findByOriginalUrl("https://google.com") } returns links
            coEvery { clickRepository.countByLinkId(1L) } returns 10L
            coEvery { clickRepository.countByLinkId(2L) } returns 20L

            val result = statsService.getStatsByUrl("https://google.com")

            result.links.size shouldBe 2
            result.totalClicks shouldBe 30L
        }

        it("링크가 없으면 NoSuchElementException을 던진다") {
            coEvery { linkRepository.findByOriginalUrl("https://unknown.com") } returns emptyList()

            shouldThrow<NoSuchElementException> {
                statsService.getStatsByUrl("https://unknown.com")
            }
        }
    }

    describe("getStatsByAuthor") {
        it("작성자의 링크 목록 통계를 반환한다") {
            val links = listOf(
                Link(id = 1L, code = "abc", originalUrl = "https://a.com", author = "ddingmin")
            )
            coEvery { linkRepository.findByAuthor("ddingmin") } returns links
            coEvery { clickRepository.countByLinkId(1L) } returns 5L

            val result = statsService.getStatsByAuthor("ddingmin")

            result.links.size shouldBe 1
            result.links[0].author shouldBe "ddingmin"
            result.totalClicks shouldBe 5L
        }

        it("작성자의 링크가 없으면 NoSuchElementException을 던진다") {
            coEvery { linkRepository.findByAuthor("nobody") } returns emptyList()

            shouldThrow<NoSuchElementException> {
                statsService.getStatsByAuthor("nobody")
            }
        }
    }
})
