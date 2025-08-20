package com.example.task.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @Schema(description = "사용자 이메일 주소", example = "user@example.com")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @Schema(description = "사용자 비밀번호", example = "password123")
    val password: String
)