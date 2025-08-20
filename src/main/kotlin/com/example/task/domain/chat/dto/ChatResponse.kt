package com.example.task.domain.chat.dto

import com.example.task.domain.chat.entity.Chat
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "채팅 응답")
data class ChatResponse(
    @Schema(description = "채팅 ID", example = "1")
    val id: Long,
    
    @Schema(description = "사용자 질문", example = "안녕하세요, 도움이 필요합니다.")
    val question: String,
    
    @Schema(description = "AI 응답", example = "안녕하세요! 무엇을 도와드릴까요?")
    val answer: String,
    
    @Schema(description = "스레드 ID", example = "1")
    val threadId: Long,
    
    @Schema(description = "사용자 ID", example = "1")
    val userId: Long,
    
    @Schema(description = "채팅 생성 시간", example = "2024-01-01T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(chat: Chat): ChatResponse {
            return ChatResponse(
                id = chat.id,
                question = chat.question,
                answer = chat.answer,
                threadId = chat.thread.id,
                userId = chat.user.id,
                createdAt = chat.createdAt
            )
        }
    }
}