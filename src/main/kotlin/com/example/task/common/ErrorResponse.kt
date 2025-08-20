package com.example.task.common

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Error response format")
data class ErrorResponse(
    @Schema(description = "HTTP status code", example = "400")
    val status: Int,
    
    @Schema(description = "Error code", example = "INVALID_REQUEST")
    val code: String,
    
    @Schema(description = "Error message", example = "Invalid input provided")
    val message: String,
    
    @Schema(description = "Request path", example = "/api/v1/auth/login")
    val path: String,
    
    @Schema(description = "Error timestamp", example = "2024-01-01T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Schema(description = "Field validation errors")
    val fieldErrors: List<FieldError>? = null
)

@Schema(description = "Field validation error")
data class FieldError(
    @Schema(description = "Field name", example = "email")
    val field: String,
    
    @Schema(description = "Field error message", example = "Invalid email format")
    val message: String
)