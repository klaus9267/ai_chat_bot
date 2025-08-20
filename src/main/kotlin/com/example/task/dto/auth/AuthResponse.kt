package com.example.task.dto.auth

import io.swagger.v3.oas.annotations.media.Schema

data class AuthResponse(
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val token: String,
    
    @Schema(description = "User email address", example = "user@example.com")
    val email: String,
    
    @Schema(description = "User display name", example = "홍길동")
    val name: String,
    
    @Schema(description = "User role", example = "MEMBER")
    val role: String
)