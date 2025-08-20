package com.example.task.dto.openai

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    @JsonProperty("max_tokens")
    val maxTokens: Int,
    val temperature: Double
)

data class OpenAIMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)