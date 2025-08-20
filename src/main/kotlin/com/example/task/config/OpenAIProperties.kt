package com.example.task.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "openai.api")
data class OpenAIProperties(
    var key: String = "",
    var url: String = "https://api.openai.com/v1",
    var model: String = "gpt-3.5-turbo",
    var maxTokens: Int = 2000,
    var temperature: Double = 0.7
)