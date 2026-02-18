package com.ddingmin.jjalb.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class HomeRouter {

    @Bean
    fun homeRoute(): RouterFunction<ServerResponse> = router {
        GET("/") {
            ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(ClassPathResource("static/index.html"))
        }
        GET("/mcp") {
            ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(ClassPathResource("static/mcp.html"))
        }
        GET("/stats") {
            ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(ClassPathResource("static/stats.html"))
        }
        GET("/stats/{code}") {
            ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(ClassPathResource("static/stats.html"))
        }
    }
}
