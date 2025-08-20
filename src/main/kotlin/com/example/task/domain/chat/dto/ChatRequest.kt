package com.example.task.domain.chat.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "채팅 요청")
data class ChatRequest(
    @field:NotBlank(message = "질문은 필수입니다")
    @field:Size(max = 2000, message = "질문은 2000자를 초과할 수 없습니다")
    @Schema(description = "사용자 질문", example = "안녕하세요, 도움이 필요합니다.")
    val message: String,
    
    @Schema(description = "스레드 ID (선택사항, 없으면 새 스레드 생성)", example = "1")
    val threadId: Long? = null
)