package com.ddingmin.jjalb.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ClickTest : DescribeSpec({

    describe("Click.of") {
        it("저장된 Link로부터 Click을 생성한다") {
            val link = Link(id = 1L, code = "abc", originalUrl = "https://google.com")
            val click = Click.of(link, referrer = "https://twitter.com", userAgent = "Mozilla/5.0")

            click.linkId shouldBe 1L
            click.referrer shouldBe "https://twitter.com"
            click.userAgent shouldBe "Mozilla/5.0"
            click.clickedAt shouldNotBe null
        }

        it("referrer와 userAgent가 null이어도 생성된다") {
            val link = Link(id = 1L, code = "abc", originalUrl = "https://google.com")
            val click = Click.of(link, referrer = null, userAgent = null)

            click.referrer shouldBe null
            click.userAgent shouldBe null
        }

        it("id가 없는 Link로 생성하면 예외를 던진다") {
            val link = Link(code = "abc", originalUrl = "https://google.com")

            shouldThrow<IllegalArgumentException> {
                Click.of(link, referrer = null, userAgent = null)
            }
        }
    }
})
