package com.example.task.application.security

import com.example.task.application.handler.exception.CustomException
import com.example.task.application.handler.exception.ErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil {
    
    companion object {
        fun getCurrentUserEmail(): String {
            val authentication = SecurityContextHolder.getContext().authentication
                ?: throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "No authentication found")
            
            return authentication.name
        }
        
        fun getCurrentUserId(): Long {
            val authentication = SecurityContextHolder.getContext().authentication
                ?: throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "No authentication found")
            
            // JWT 필터에서 설정한 details에서 userId 추출
            val details = authentication.details as? Map<String, Any>
                ?: throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "User details not found")
            
            return details["userId"] as? Long
                ?: throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "User ID not found in authentication")
        }
        
        fun getCurrentUserRole(): String {
            val authentication = SecurityContextHolder.getContext().authentication
                ?: throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "No authentication found")
            
            val authorities = authentication.authorities
            val roleAuthority = authorities.firstOrNull { it.authority.startsWith("ROLE_") }
                ?: throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "User role not found")
            
            // "ROLE_" 접두사 제거
            return roleAuthority.authority.removePrefix("ROLE_")
        }
        
        fun getCurrentUserInfo(): UserInfo {
            return UserInfo(
                email = getCurrentUserEmail(),
                userId = getCurrentUserId(),
                role = getCurrentUserRole()
            )
        }
        
        fun isAuthenticated(): Boolean {
            val authentication = SecurityContextHolder.getContext().authentication
            return authentication != null && authentication.isAuthenticated && authentication.name != "anonymousUser"
        }
    }
}

data class UserInfo(
    val email: String,
    val userId: Long,
    val role: String
)