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

        it("_ 접두사는 임시 코드로 판단한다") {
            ShortCode("_temp1234").isTemporary shouldBe true
        }

        it("일반 코드는 임시가 아니다") {
            ShortCode("abc123").isTemporary shouldBe false
        }
    }
})
