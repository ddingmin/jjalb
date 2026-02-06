package com.ddingmin.jjalb.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class Base62CodeGeneratorTest : DescribeSpec({

    val generator = Base62CodeGenerator()

    describe("generate") {
        it("ID 1을 '1'로 변환한다") {
            generator.generate(1).value shouldBe "1"
        }

        it("ID 62를 '10'으로 변환한다") {
            generator.generate(62).value shouldBe "10"
        }

        it("단일 자릿수 범위를 올바르게 인코딩한다") {
            generator.generate(9).value shouldBe "9"
            generator.generate(10).value shouldBe "a"
            generator.generate(35).value shouldBe "z"
            generator.generate(36).value shouldBe "A"
            generator.generate(61).value shouldBe "Z"
        }

        it("큰 값을 올바르게 인코딩한다") {
            generator.generate(100).value shouldBe "1C"
            generator.generate(1000).value shouldBe "g8"
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

    describe("tempCode") {
        it("_ 접두사로 시작하는 임시 코드를 생성한다") {
            generator.tempCode().value shouldStartWith "_"
        }

        it("호출할 때마다 다른 코드를 생성한다") {
            val codes = (1..10).map { generator.tempCode().value }.toSet()
            (codes.size shouldBe 10)
        }
    }
})
