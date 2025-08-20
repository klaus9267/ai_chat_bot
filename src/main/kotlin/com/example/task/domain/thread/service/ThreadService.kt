package com.example.task.domain.thread.service

import com.example.task.application.security.SecurityUtil
import com.example.task.application.handler.exception.CustomException
import com.example.task.application.handler.exception.ErrorCode
import com.example.task.domain.thread.entity.Thread
import com.example.task.domain.thread.repository.ThreadRepository
import com.example.task.domain.user.entity.User
import com.example.task.domain.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ThreadService(
    private val threadRepository: ThreadRepository,
    private val userService: UserService
) {

    companion object {
        const val THREAD_TIMEOUT_MINUTES = 30L
    }

    @Transactional
    fun getOrCreateActiveThread(userId: Long): Thread {
        val user = userService.findById(userId)
            ?: throw CustomException(ErrorCode.USER_NOT_FOUND, "User not found: $userId")

        val cutoffTime = LocalDateTime.now().minusMinutes(THREAD_TIMEOUT_MINUTES)

        // 30분 이내에 활동한 스레드가 있으면 해당 스레드 반환
        val activeThread = threadRepository.findActiveThreadByUserId(userId, cutoffTime)
        if (activeThread.isPresent) {
            return activeThread.get()
        }

        // 없으면 새 스레드 생성
        return createNewThread(user)
    }

    @Transactional
    fun createNewThread(user: User): Thread {
        val thread = Thread(user = user)
        return threadRepository.save(thread)
    }

    fun findThreadsByUserId(userId: Long): List<Thread> {
        return threadRepository.findByUserIdOrderByUpdatedAtDesc(userId)
    }

    fun getThreadsForUser(userId: Long, userRole: String): List<Thread> {
        return if (userRole == "ADMIN") {
            getAllThreads()
        } else {
            findThreadsByUserId(userId)
        }
    }

    fun findThreadById(threadId: Long, userId: Long): Thread {
        return threadRepository.findByIdAndUserId(threadId, userId)
            .orElseThrow {
                CustomException(ErrorCode.THREAD_NOT_FOUND, "Thread not found: $threadId")
            }
    }

    fun getThreadForUser(threadId: Long, userId: Long, userRole: String): Thread {
        // 권한 검사
        if (!canUserAccessThread(threadId, userId, userRole)) {
            throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "Cannot access this thread")
        }

        return if (userRole == "ADMIN") {
            threadRepository.findById(threadId)
                .orElseThrow {
                    CustomException(ErrorCode.THREAD_NOT_FOUND, "Thread not found: $threadId")
                }
        } else {
            findThreadById(threadId, userId)
        }
    }

    @Transactional
    fun deleteThread(threadId: Long, userId: Long) {
        val thread = findThreadById(threadId, userId)
        threadRepository.delete(thread)
    }

    @Transactional
    fun deleteThreadForUser(threadId: Long, userId: Long, userRole: String) {
        // 권한 검사
        if (userRole != "ADMIN" && !canUserAccessThread(threadId, userId, userRole)) {
            throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "Cannot delete this thread")
        }

        if (userRole == "ADMIN") {
            // Admin의 경우 스레드를 찾아서 실제 소유자 ID로 삭제
            val thread = threadRepository.findById(threadId)
                .orElseThrow {
                    CustomException(ErrorCode.THREAD_NOT_FOUND, "Thread not found: $threadId")
                }
            threadRepository.delete(thread)
        } else {
            deleteThread(threadId, userId)
        }
    }

    fun getAllThreads(): List<Thread> {
        return threadRepository.findAll()
    }

    fun canUserAccessThread(threadId: Long, userId: Long, userRole: String): Boolean {
        return if (userRole == "ADMIN") {
            threadRepository.existsById(threadId)
        } else {
            threadRepository.findByIdAndUserId(threadId, userId).isPresent
        }
    }

    @Transactional
    fun updateThreadTimestamp(threadId: Long) {
        val thread = threadRepository.findById(threadId)
            .orElseThrow {
                CustomException(ErrorCode.THREAD_NOT_FOUND, "Thread not found: $threadId")
            }

        threadRepository.save(thread)
    }

    // SecurityUtil을 사용하는 더 깔끔한 메서드들
    fun getCurrentUserThreads(): List<Thread> {
        val userInfo = SecurityUtil.Companion.getCurrentUserInfo()
        return getThreadsForUser(userInfo.userId, userInfo.role)
    }

    fun getCurrentUserThread(threadId: Long): Thread {
        val userInfo = SecurityUtil.Companion.getCurrentUserInfo()
        return getThreadForUser(threadId, userInfo.userId, userInfo.role)
    }

    @Transactional
    fun deleteCurrentUserThread(threadId: Long) {
        val userInfo = SecurityUtil.Companion.getCurrentUserInfo()
        deleteThreadForUser(threadId, userInfo.userId, userInfo.role)
    }

    @Transactional
    fun getCurrentUserActiveThread(): Thread {
        val userId = SecurityUtil.Companion.getCurrentUserId()
        return getOrCreateActiveThread(userId)
    }
    
    /**
     * 대화 목록 조회 (페이지네이션 + 권한 체크)
     */
    fun getUserThreads(targetUserId: Long?, pageable: Pageable): Page<Thread> {
        val currentUserInfo = SecurityUtil.Companion.getCurrentUserInfo()
        
        return when {
            // userId가 없으면 현재 사용자의 스레드 조회
            targetUserId == null -> {
                threadRepository.findByUserIdOrderByUpdatedAtDesc(currentUserInfo.userId, pageable)
            }
            
            // 자신의 스레드를 조회하는 경우
            targetUserId == currentUserInfo.userId -> {
                threadRepository.findByUserIdOrderByUpdatedAtDesc(currentUserInfo.userId, pageable)
            }
            
            // 다른 사용자의 스레드를 조회하는 경우
            else -> {
                // 관리자만 다른 사용자의 스레드 조회 가능
                if (currentUserInfo.role != "ADMIN") {
                    throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "다른 사용자의 대화를 조회할 권한이 없습니다")
                }
                
                // 대상 사용자 존재 확인
                userService.findById(targetUserId)
                    ?: throw CustomException(ErrorCode.USER_NOT_FOUND, "대상 사용자를 찾을 수 없습니다: $targetUserId")
                
                threadRepository.findByUserIdOrderByUpdatedAtDesc(targetUserId, pageable)
            }
        }
    }
    
}