package com.ddingmin.jjalb.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ShortCodeTest : DescribeSpec({

    describe("ShortCode") {
        it("유효한 코드를 생성한다") {
            ShortCode("abc123").value shouldBe "abc123"
        }

        it("빈 문자열이면 예외를 던진다") {
            shouldThrow<IllegalArgumentException> {
                ShortCode("")
            }
        }

        it("10자 이하 코드는 허용한다") {
            ShortCode("abcdefghij").value shouldBe "abcdefghij"
        }

        it("10자를 초과하면 예외를 던진다") {
            shouldThrow<IllegalArgumentException> {
                ShortCode("abcdefghijk")
            }
        }

        it("허용되지 않은 문자가 포함되면 예외를 던진다") {
            shouldThrow<IllegalArgumentException> {
                ShortCode("abc-123")
            }
            shouldThrow<IllegalArgumentException> {
                ShortCode("abc_123")
            }
        }
    }
})
