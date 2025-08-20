package com.example.task.controller

import com.example.task.application.UserService
import com.example.task.common.exception.CustomException
import com.example.task.common.exception.ErrorCode
import com.example.task.config.JwtUtil
import com.example.task.dto.auth.AuthResponse
import com.example.task.dto.auth.LoginRequest
import com.example.task.dto.auth.SignupRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증", description = "사용자 인증 API")
class AuthController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자 계정을 등록합니다")
    fun signup(@Valid @RequestBody request: SignupRequest): AuthResponse {
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

        return AuthResponse(
            token = token,
            email = user.email,
            name = user.name,
            role = user.role.toString()
        )
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 반환합니다")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse {
        val user = userService.findByEmail(request.email)
            ?: throw CustomException(ErrorCode.INVALID_CREDENTIALS)

        if (!userService.validatePassword(user, request.password)) {
            throw CustomException(ErrorCode.INVALID_CREDENTIALS)
        }

        val token = jwtUtil.generateToken(
            userEmail = user.email,
            userId = user.id,
            role = user.role.toString()
        )

        return AuthResponse(
            token = token,
            email = user.email,
            name = user.name,
            role = user.role.toString()
        )
    }
}