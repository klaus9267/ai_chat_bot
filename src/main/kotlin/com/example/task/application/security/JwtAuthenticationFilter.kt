package com.example.task.application.security

import com.example.task.application.security.JwtUtil
import com.example.task.domain.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val jwt = authHeader.substring(7)

            try {
                val userEmail = jwtUtil.getEmailFromToken(jwt)

                if (SecurityContextHolder.getContext().authentication == null) {
                    // 토큰 유효성 검증 + 사용자 존재 확인
                    val user = userRepository.findByEmail(userEmail).orElse(null)
                    if (user != null && jwtUtil.validateToken(jwt, userEmail)) {
                        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
                        val authToken = UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            authorities
                        )
                        
                        // SecurityContext에 사용자 정보 저장 (SecurityUtil에서 사용)
                        val userDetails = mapOf(
                            "userId" to user.id,
                            "userRole" to user.role.toString()
                        )
                        authToken.details = userDetails
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            } catch (e: Exception) {
                // Invalid JWT token, continue without authentication
                logger.debug("JWT token validation failed: ${e.message}")
            }
        }

        filterChain.doFilter(request, response)
    }
}