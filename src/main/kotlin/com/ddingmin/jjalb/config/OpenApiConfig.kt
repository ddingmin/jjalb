package com.ddingmin.jjalb.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("jjalb API")
                .description("URL 단축 및 클릭 통계 서비스 API")
                .version("1.0.0")
                .contact(
                    Contact()
                        .name("ddingmin")
                )
        )
}
