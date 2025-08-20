package com.example.task.dto.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @Schema(description = "User email address", example = "user@example.com")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "password123")
    val password: String
)