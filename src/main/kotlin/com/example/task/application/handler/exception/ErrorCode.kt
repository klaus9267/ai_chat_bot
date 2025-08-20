package com.example.task.application.handler.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val message: String,
    val status: HttpStatus
) {
    // Authentication & Authorization
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "Email already exists", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "Unauthorized access", HttpStatus.FORBIDDEN),
    
    // Chat & Thread
    CHAT_NOT_FOUND("CHAT_NOT_FOUND", "Chat not found", HttpStatus.NOT_FOUND),
    THREAD_NOT_FOUND("THREAD_NOT_FOUND", "Thread not found", HttpStatus.NOT_FOUND),
    
    // Feedback
    DUPLICATE_FEEDBACK("DUPLICATE_FEEDBACK", "Feedback already exists for this chat", HttpStatus.CONFLICT),
    FEEDBACK_NOT_FOUND("FEEDBACK_NOT_FOUND", "Feedback not found", HttpStatus.NOT_FOUND),
    
    // General
    INVALID_REQUEST("INVALID_REQUEST", "Invalid request", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // OpenAI API
    OPENAI_API_ERROR("OPENAI_API_ERROR", "OpenAI API error", HttpStatus.BAD_GATEWAY),
    OPENAI_RATE_LIMIT("OPENAI_RATE_LIMIT", "OpenAI API rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS),
    
    // File & Report
    FILE_PROCESSING_ERROR("FILE_PROCESSING_ERROR", "File processing error", HttpStatus.INTERNAL_SERVER_ERROR),
    REPORT_GENERATION_ERROR("REPORT_GENERATION_ERROR", "Report generation error", HttpStatus.INTERNAL_SERVER_ERROR)
}