package com.example.task.domain.ai.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenAIResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<OpenAIChoice>,
    val usage: OpenAIUsage?
)

data class OpenAIChoice(
    val index: Int,
    val message: OpenAIMessage,
    @JsonProperty("finish_reason")
    val finishReason: String?
)

data class OpenAIUsage(
    @JsonProperty("prompt_tokens")
    val promptTokens: Int,
    @JsonProperty("completion_tokens")
    val completionTokens: Int,
    @JsonProperty("total_tokens")
    val totalTokens: Int
)