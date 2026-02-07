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
        it("ID 기반으로 Sqids 코드를 생성하고 링크를 저장한다") {
            val originalUrl = OriginalUrl("https://google.com")
            val pendingLink = Link(id = 1L, code = null, originalUrl = "https://google.com")
            val generatedCode = ShortCode("Xr04lY")
            val finalLink = Link(id = 1L, code = "Xr04lY", originalUrl = "https://google.com")

            coEvery { linkRepository.save(match { it.code == null }) } returns pendingLink
            coEvery { codeGenerator.generate(1L) } returns generatedCode
            coEvery { linkRepository.save(match { it.code == "Xr04lY" }) } returns finalLink

            val result = linkService.shorten(originalUrl)

            result.code shouldBe "Xr04lY"
            coVerify(exactly = 2) { linkRepository.save(any()) }
            coVerify { linkCacheRepository.put(ShortCode("Xr04lY"), originalUrl) }
        }
    }

    describe("redirect") {
        it("URL을 반환하고 ClickEvent를 발행한다") {
            val code = ShortCode("abc12345")
            val cachedUrl = OriginalUrl("https://google.com")
            val eventSlot = slot<ClickEvent>()

            coEvery { linkCacheRepository.get(code) } returns cachedUrl
            coEvery { eventPublisher.publishEvent(capture(eventSlot)) } returns Unit

            val result = linkService.redirect(code, "https://twitter.com", "Mozilla/5.0")

            result shouldBe cachedUrl
            eventSlot.captured.code shouldBe "abc12345"
            eventSlot.captured.referrer shouldBe "https://twitter.com"
            eventSlot.captured.userAgent shouldBe "Mozilla/5.0"
        }

        it("존재하지 않는 코드이면 예외를 던진다") {
            val code = ShortCode("unknown1")
            coEvery { linkCacheRepository.get(code) } returns null
            coEvery { linkRepository.findByCode("unknown1") } returns null

            shouldThrow<NoSuchElementException> {
                linkService.redirect(code, null, null)
            }

            verify(exactly = 0) { eventPublisher.publishEvent(any()) }
        }
    }

    describe("resolve") {
        val code = ShortCode("abc12345")

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
                val link = Link(id = 1L, code = "abc12345", originalUrl = "https://google.com")
                coEvery { linkCacheRepository.get(code) } returns null
                coEvery { linkRepository.findByCode("abc12345") } returns link

                val result = linkService.resolve(code)

                result shouldBe OriginalUrl("https://google.com")
                coVerify { linkCacheRepository.put(code, OriginalUrl("https://google.com")) }
            }
        }

        context("캐시에도 DB에도 없을 때") {
            it("null을 반환한다") {
                coEvery { linkCacheRepository.get(code) } returns null
                coEvery { linkRepository.findByCode("abc12345") } returns null

                val result = linkService.resolve(code)

                result shouldBe null
            }
        }
    }
})
