package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.ClickEvent
import com.ddingmin.jjalb.domain.Link
import com.ddingmin.jjalb.domain.OriginalUrl
import com.ddingmin.jjalb.domain.ShortCode
import com.ddingmin.jjalb.repository.LinkRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher

class LinkServiceTest : DescribeSpec({

    val linkRepository = mockk<LinkRepository>()
    val codeGenerator = mockk<CodeGenerator>()
    val linkCacheRepository = mockk<LinkCacheRepository>(relaxUnitFun = true)
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxUnitFun = true)

    val linkService = LinkService(linkRepository, codeGenerator, linkCacheRepository, eventPublisher)

    beforeEach { clearAllMocks() }

    describe("shorten") {
        it("임시 코드로 저장 후 ID 기반 코드로 갱신한다") {
            val tempCode = ShortCode("_abc12345")
            val generatedCode = ShortCode("1")
            val originalUrl = OriginalUrl("https://google.com")

            val tempLink = Link(id = 1L, code = "_abc12345", originalUrl = "https://google.com")
            val finalLink = Link(id = 1L, code = "1", originalUrl = "https://google.com")

            coEvery { codeGenerator.tempCode() } returns tempCode
            coEvery { codeGenerator.generate(1L) } returns generatedCode
            coEvery { linkRepository.save(any()) } returnsMany listOf(tempLink, finalLink)

            val result = linkService.shorten(originalUrl)

            result.code shouldBe "1"
            coVerify(exactly = 2) { linkRepository.save(any()) }
            coVerify { linkCacheRepository.put(ShortCode("1"), originalUrl) }
        }
    }

    describe("redirect") {
        it("URL을 반환하고 ClickEvent를 발행한다") {
            val code = ShortCode("abc")
            val cachedUrl = OriginalUrl("https://google.com")
            val eventSlot = slot<ClickEvent>()

            coEvery { linkCacheRepository.get(code) } returns cachedUrl
            coEvery { eventPublisher.publishEvent(capture(eventSlot)) } returns Unit

            val result = linkService.redirect(code, "https://twitter.com", "Mozilla/5.0")

            result shouldBe cachedUrl
            eventSlot.captured.code shouldBe "abc"
            eventSlot.captured.referrer shouldBe "https://twitter.com"
            eventSlot.captured.userAgent shouldBe "Mozilla/5.0"
        }

        it("존재하지 않는 코드이면 예외를 던진다") {
            val code = ShortCode("unknown")
            coEvery { linkCacheRepository.get(code) } returns null
            coEvery { linkRepository.findByCode("unknown") } returns null

            shouldThrow<NoSuchElementException> {
                linkService.redirect(code, null, null)
            }

            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }
    }

    describe("resolve") {
        val code = ShortCode("abc")

        context("캐시에 있을 때") {
            it("캐시에서 URL을 반환한다") {
                val cachedUrl = OriginalUrl("https://google.com")
                coEvery { linkCacheRepository.get(code) } returns cachedUrl

                val result = linkService.resolve(code)

                result shouldBe cachedUrl
            }
        }

        context("캐시에 없고 DB에 있을 때") {
            it("DB에서 조회 후 캐시에 저장하고 반환한다") {
                val link = Link(id = 1L, code = "abc", originalUrl = "https://google.com")
                coEvery { linkCacheRepository.get(code) } returns null
                coEvery { linkRepository.findByCode("abc") } returns link

                val result = linkService.resolve(code)

                result shouldBe OriginalUrl("https://google.com")
                coVerify { linkCacheRepository.put(code, OriginalUrl("https://google.com")) }
            }
        }

        context("캐시에도 DB에도 없을 때") {
            it("null을 반환한다") {
                coEvery { linkCacheRepository.get(code) } returns null
                coEvery { linkRepository.findByCode("abc") } returns null

                val result = linkService.resolve(code)

                result shouldBe null
            }
        }
    }
})
