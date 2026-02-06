package com.ddingmin.jjalb.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class OriginalUrlTest : DescribeSpec({

    describe("OriginalUrl.from") {
        it("https:// 접두사가 있으면 그대로 유지한다") {
            OriginalUrl.from("https://google.com").value shouldBe "https://google.com"
        }

        it("http:// 접두사가 있으면 그대로 유지한다") {
            OriginalUrl.from("http://google.com").value shouldBe "http://google.com"
        }

        it("스킴이 없으면 https://를 붙인다") {
            OriginalUrl.from("google.com").value shouldBe "https://google.com"
        }
    }

    describe("OriginalUrl 생성자") {
        it("빈 문자열이면 예외를 던진다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("")
            }
        }

        it("공백 문자열이면 예외를 던진다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("   ")
            }
        }
    }
})
