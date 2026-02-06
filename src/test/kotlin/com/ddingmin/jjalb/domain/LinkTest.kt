package com.ddingmin.jjalb.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class LinkTest : DescribeSpec({

    describe("Link.create") {
        it("ShortCode와 OriginalUrl로 Link를 생성한다") {
            val link = Link.create(
                code = ShortCode("abc"),
                originalUrl = OriginalUrl("https://google.com")
            )

            link.code shouldBe "abc"
            link.originalUrl shouldBe "https://google.com"
            link.id shouldBe null
            link.createdAt shouldNotBe null
        }
    }

    describe("Link.assignCode") {
        it("새로운 코드가 할당된 Link를 반환한다") {
            val link = Link(id = 1L, code = "_temp", originalUrl = "https://google.com")
            val updated = link.assignCode(ShortCode("1"))

            updated.code shouldBe "1"
            updated.id shouldBe 1L
            updated.originalUrl shouldBe "https://google.com"
        }
    }

    describe("Link.shortCode") {
        it("code 필드를 ShortCode 값 객체로 변환한다") {
            val link = Link(code = "abc", originalUrl = "https://google.com")
            link.shortCode shouldBe ShortCode("abc")
        }
    }
})
