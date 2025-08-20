package com.example.task.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema

data class AuthResponse(
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val token: String,
    
    @Schema(description = "사용자 이메일 주소", example = "user@example.com")
    val email: String,
    
    @Schema(description = "사용자 디스플레이 이름", example = "홍길동")
    val name: String,
    
    @Schema(description = "사용자 역할", example = "MEMBER")
    val role: String
)