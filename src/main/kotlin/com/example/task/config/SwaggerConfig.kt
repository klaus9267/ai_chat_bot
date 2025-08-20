package com.example.task.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.text.trimMargin

@Configuration
class SwaggerConfig {

    @Bean
    fun openApi(): OpenAPI {
        val securityRequirement = SecurityRequirement().addList("bearerAuth")

        return OpenAPI()
            .info(
                Info()
                    .version("1.0.0")
                    .title("AI CHAT BOT API 명세서")
            )
            .addSecurityItem(securityRequirement)
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
    }
}
