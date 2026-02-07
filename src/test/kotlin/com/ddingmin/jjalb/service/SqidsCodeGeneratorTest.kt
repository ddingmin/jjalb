package com.ddingmin.jjalb.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch

class SqidsCodeGeneratorTest : DescribeSpec({

    val alphabet = "k3G7QAe51FCsPW92uEOyq4Bg6Sp8YzVTmnU0liwDdHXLajZrfxNhobJIRcMvKt"
    val minLength = 6
    val generator = SqidsCodeGenerator(alphabet, minLength)

    describe("generate") {
        it("ID로부터 최소 길이 이상의 코드를 생성한다") {
            val code = generator.generate(1L)
            code.value.length shouldBeGreaterThanOrEqualTo minLength
        }

        it("동일 ID는 항상 동일 코드를 생성한다") {
            val code1 = generator.generate(42L)
            val code2 = generator.generate(42L)
            code1 shouldBe code2
        }

        it("서로 다른 ID는 서로 다른 코드를 생성한다") {
            val codes = (1L..100L).map { generator.generate(it).value }.toSet()
            codes.size shouldBe 100
        }

        it("영숫자 문자만 포함한다") {
            val code = generator.generate(12345L)
            code.value shouldMatch "^[0-9a-zA-Z]+$"
        }

        it("0 이하 값이면 예외를 던진다") {
            shouldThrow<IllegalArgumentException> {
                generator.generate(0)
            }
            shouldThrow<IllegalArgumentException> {
                generator.generate(-1)
            }
        }
    }
})
