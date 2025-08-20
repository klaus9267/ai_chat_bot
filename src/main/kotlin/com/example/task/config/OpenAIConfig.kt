package com.example.task.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class OpenAIConfig(
    private val openAIProperties: OpenAIProperties
) {

    @Bean(name = "openAIWebClient")
    fun openAIWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(openAIProperties.url)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${openAIProperties.key}")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}