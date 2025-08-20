package com.example.task.dto.chat

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "채팅 히스토리 응답")
data class ChatHistoryResponse(
    @Schema(description = "스레드 ID", example = "1")
    val threadId: Long,
    
    @Schema(description = "채팅 목록")
    val chats: List<ChatResponse>,
    
    @Schema(description = "총 채팅 수", example = "5")
    val totalCount: Int
) {
    companion object {
        fun from(threadId: Long, chats: List<ChatResponse>): ChatHistoryResponse {
            return ChatHistoryResponse(
                threadId = threadId,
                chats = chats,
                totalCount = chats.size
            )
        }
    }
}