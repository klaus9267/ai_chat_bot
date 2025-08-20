package com.example.task.dto.thread

import com.example.task.domain.thread.Thread
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "스레드 응답")
data class ThreadResponse(
    @Schema(description = "스레드 ID", example = "1")
    val id: Long,
    
    @Schema(description = "이 스레드를 소유한 사용자 ID", example = "1")
    val userId: Long,
    
    @Schema(description = "스레드 생성 시간", example = "2024-01-01T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,
    
    @Schema(description = "스레드 마지막 업데이트 시간", example = "2024-01-01T11:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(thread: Thread): ThreadResponse {
            return ThreadResponse(
                id = thread.id,
                userId = thread.user.id,
                createdAt = thread.createdAt,
                updatedAt = thread.updatedAt
            )
        }
    }
}