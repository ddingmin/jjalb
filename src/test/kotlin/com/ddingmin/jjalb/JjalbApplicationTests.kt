package com.ddingmin.jjalb

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.DescribeSpec

@Ignored
class JjalbApplicationTests : DescribeSpec({

    describe("contextLoads") {
        it("애플리케이션 컨텍스트가 로드된다") {
            // Requires PostgreSQL and Redis
        }
    }
})
