package com.ddingmin.jjalb.service

import com.ddingmin.jjalb.domain.Click
import com.ddingmin.jjalb.domain.Link
import com.ddingmin.jjalb.repository.ClickRepository
import com.ddingmin.jjalb.repository.LinkRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot

class ClickServiceTest : DescribeSpec({

    val clickRepository = mockk<ClickRepository>()
    val linkRepository = mockk<LinkRepository>()
    val clickService = ClickService(clickRepository, linkRepository)

    beforeEach { clearAllMocks() }

    describe("record") {
        it("존재하는 코드에 대해 클릭을 기록한다") {
            val link = Link(id = 1L, code = "abc", originalUrl = "https://google.com")
            val clickSlot = slot<Click>()

            coEvery { linkRepository.findByCode("abc") } returns link
            coEvery { clickRepository.save(capture(clickSlot)) } answers { clickSlot.captured }

            clickService.record("abc", "https://twitter.com", "Mozilla/5.0")

            clickSlot.captured.linkId shouldBe 1L
            clickSlot.captured.referrer shouldBe "https://twitter.com"
            clickSlot.captured.userAgent shouldBe "Mozilla/5.0"
        }

        it("존재하지 않는 코드이면 아무것도 하지 않는다") {
            coEvery { linkRepository.findByCode("unknown") } returns null

            clickService.record("unknown", null, null)

            coVerify(exactly = 0) { clickRepository.save(any()) }
        }
    }
})
