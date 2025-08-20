package com.example.task.common

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "오류 응답 형식")
data class ErrorResponse(
    @Schema(description = "HTTP 상태 코드", example = "400")
    val status: Int,
    
    @Schema(description = "오류 코드", example = "INVALID_REQUEST")
    val code: String,
    
    @Schema(description = "오류 메시지", example = "Invalid input provided")
    val message: String,
    
    @Schema(description = "요청 경로", example = "/api/v1/auth/login")
    val path: String,
    
    @Schema(description = "오류 발생 시간", example = "2024-01-01T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Schema(description = "필드 유효성 검사 오류")
    val fieldErrors: List<FieldError>? = null
)

@Schema(description = "필드 유효성 검사 오류")
data class FieldError(
    @Schema(description = "필드 이름", example = "email")
    val field: String,
    
    @Schema(description = "필드 오류 메시지", example = "Invalid email format")
    val message: String
)