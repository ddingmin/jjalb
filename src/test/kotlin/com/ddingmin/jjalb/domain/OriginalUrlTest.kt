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

        it("쿼리 파라미터가 포함된 URL을 허용한다") {
            OriginalUrl.from("https://google.com/search?q=test").value shouldBe "https://google.com/search?q=test"
        }

        it("프래그먼트가 포함된 URL을 허용한다") {
            OriginalUrl.from("https://example.com/page#section").value shouldBe "https://example.com/page#section"
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

        it("2048자를 초과하면 예외를 던진다") {
            val longUrl = "https://example.com/" + "a".repeat(2048)
            shouldThrow<IllegalArgumentException> {
                OriginalUrl(longUrl)
            }
        }
    }

    describe("프로토콜 검증") {
        it("javascript: 프로토콜을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("javascript:alert('xss')")
            }
        }

        it("ftp:// 프로토콜을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("ftp://files.example.com/file.txt")
            }
        }

        it("file:// 프로토콜을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("file:///etc/passwd")
            }
        }

        it("data: 프로토콜을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("data:text/html,<script>alert('xss')</script>")
            }
        }
    }

    describe("내부 IP 차단") {
        it("localhost를 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("https://localhost/admin")
            }
        }

        it("127.0.0.1을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("https://127.0.0.1/admin")
            }
        }

        it("10.x.x.x 대역을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("https://10.0.0.1/internal")
            }
        }

        it("192.168.x.x 대역을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("https://192.168.1.1/router")
            }
        }

        it("0.0.0.0을 거부한다") {
            shouldThrow<IllegalArgumentException> {
                OriginalUrl("https://0.0.0.0/")
            }
        }
    }
})
