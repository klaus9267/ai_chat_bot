package com.example.task.application

import com.example.task.common.exception.CustomException
import com.example.task.common.exception.ErrorCode
import com.example.task.domain.user.User
import com.example.task.domain.user.UserRepository
import com.example.task.domain.user.UserRole
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(email: String, password: String, name: String): User {
        if (userRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.DUPLICATE_EMAIL, "Email already exists: $email")
        }
        
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(
            email = email,
            password = encodedPassword,
            name = name,
            role = UserRole.MEMBER
        )
        
        return userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }

    fun findById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun validatePassword(user: User, rawPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, user.password)
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}