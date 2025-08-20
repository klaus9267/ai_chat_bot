package com.example.task.dto.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @Schema(description = "User email address", example = "user@example.com")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Schema(description = "User password (6-20 characters)", example = "password123")
    val password: String,

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(description = "User display name", example = "홍길동")
    val name: String
)