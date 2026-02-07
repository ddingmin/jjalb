package com.ddingmin.jjalb.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class LinkTest : DescribeSpec({

    describe("Link.create") {
        it("ShortCode와 OriginalUrl로 Link를 생성한다") {
            val link = Link.create(
                code = ShortCode("abc123"),
                originalUrl = OriginalUrl("https://google.com")
            )

            link.code shouldBe "abc123"
            link.originalUrl shouldBe "https://google.com"
            link.id shouldBe null
            link.createdAt shouldNotBe null
        }
    }

    describe("Link.createPending") {
        it("코드 없이 Link를 생성한다") {
            val link = Link.createPending(OriginalUrl("https://google.com"))

            link.code shouldBe null
            link.originalUrl shouldBe "https://google.com"
            link.id shouldBe null
        }
    }

    describe("Link.assignCode") {
        it("새로운 코드가 할당된 Link를 반환한다") {
            val link = Link(id = 1L, code = null, originalUrl = "https://google.com")
            val updated = link.assignCode(ShortCode("abc123"))

            updated.code shouldBe "abc123"
            updated.id shouldBe 1L
            updated.originalUrl shouldBe "https://google.com"
        }
    }

    describe("Link.shortCode") {
        it("code 필드를 ShortCode 값 객체로 변환한다") {
            val link = Link(code = "abc123", originalUrl = "https://google.com")
            link.shortCode shouldBe ShortCode("abc123")
        }

        it("code가 null이면 예외를 던진다") {
            val link = Link(code = null, originalUrl = "https://google.com")
            shouldThrow<IllegalStateException> {
                link.shortCode
            }
        }
    }
})
