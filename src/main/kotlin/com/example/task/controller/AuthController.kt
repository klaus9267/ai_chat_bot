package com.example.task.controller

import com.example.task.application.UserService
import com.example.task.config.JwtUtil
import com.example.task.dto.auth.AuthResponse
import com.example.task.dto.auth.LoginRequest
import com.example.task.dto.auth.SignupRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication APIs")
class AuthController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/signup")
    @Operation(summary = "User signup", description = "Register a new user account")
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<AuthResponse> {
        try {
            val user = userService.createUser(
                email = request.email,
                password = request.password,
                name = request.name
            )

            val token = jwtUtil.generateToken(
                userEmail = user.email,
                userId = user.id,
                role = user.role.toString()
            )

            val response = AuthResponse(
                token = token,
                email = user.email,
                name = user.name,
                role = user.role.toString()
            )

            return ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val user = userService.findByEmail(request.email)
            ?: return ResponseEntity.badRequest().build()

        if (!userService.validatePassword(user, request.password)) {
            return ResponseEntity.badRequest().build()
        }

        val token = jwtUtil.generateToken(
            userEmail = user.email,
            userId = user.id,
            role = user.role.toString()
        )

        val response = AuthResponse(
            token = token,
            email = user.email,
            name = user.name,
            role = user.role.toString()
        )

        return ResponseEntity.ok(response)
    }
}