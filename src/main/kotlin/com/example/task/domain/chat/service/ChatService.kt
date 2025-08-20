package com.example.task.domain.chat.service

import com.example.task.application.handler.exception.CustomException
import com.example.task.application.handler.exception.ErrorCode
import com.example.task.application.security.SecurityUtil
import com.example.task.domain.ai.service.OpenAIService
import com.example.task.domain.chat.entity.Chat
import com.example.task.domain.chat.repository.ChatRepository
import com.example.task.domain.thread.service.ThreadService
import com.example.task.domain.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRepository: ChatRepository,
    private val threadService: ThreadService,
    private val userService: UserService,
    private val openAIService: OpenAIService
) {

    companion object {
        const val CONTEXT_LIMIT = 10 // 이전 대화 컨텍스트 개수
    }

    /**
     * 새로운 채팅 생성 및 AI 응답 생성
     */
    @Transactional
    fun createChat(message: String): Chat {
        val userInfo = SecurityUtil.Companion.getCurrentUserInfo()
        val user = userService.findById(userInfo.userId)
            ?: throw CustomException(ErrorCode.USER_NOT_FOUND, "User not found: ${userInfo.userId}")

        // 스레드 가져오기 또는 생성
        val thread = threadService.getCurrentUserActiveThread()

        // AI 응답 생성
        val chatHistory = getRecentChats(thread.id)
        val aiResponse = openAIService.generateResponse(message, chatHistory)

        // 채팅 저장
        val chat = Chat(
            question = message,
            answer = aiResponse,
            user = user,
            thread = thread
        )

        val savedChat = chatRepository.save(chat)

        // 스레드 타임스탬프 업데이트
        threadService.updateThreadTimestamp(thread.id)

        return savedChat
    }

    /**
     * 특정 스레드의 최근 채팅 컨텍스트 가져오기
     */
    private fun getRecentChats(threadId: Long): List<Chat> {
        val recentChats = chatRepository.findRecentChatsByThreadId(
            threadId,
            PageRequest.of(0, CONTEXT_LIMIT)
        )
        // 시간 순서대로 정렬 (가장 오래된 것부터)
        return recentChats.reversed()
    }

    /**
     * 특정 스레드의 채팅 히스토리 조회
     */
    fun getChatHistory(threadId: Long): List<Chat> {
        // 권한 체크: 현재 사용자가 이 스레드에 접근할 수 있는지 확인
        threadService.getCurrentUserThread(threadId)

        return chatRepository.findByThreadIdOrderByCreatedAtAsc(threadId)
    }

    /**
     * 특정 스레드의 채팅 히스토리 조회 (페이징)
     */
    fun getChatHistory(threadId: Long, pageable: Pageable): Page<Chat> {
        // 권한 체크
        threadService.getCurrentUserThread(threadId)

        return chatRepository.findByThreadIdOrderByCreatedAtDesc(threadId, pageable)
    }

    /**
     * 현재 사용자의 모든 채팅 조회
     */
    fun getCurrentUserChats(): List<Chat> {
        val userId = SecurityUtil.Companion.getCurrentUserId()
        return chatRepository.findByUserIdOrderByCreatedAtDesc(userId)
    }

    /**
     * 특정 채팅 조회
     */
    fun getChatById(chatId: Long): Chat {
        val chat = chatRepository.findById(chatId)
            .orElseThrow { CustomException(ErrorCode.RESOURCE_NOT_FOUND, "채팅을 찾을 수 없습니다: $chatId") }

        // 권한 체크: 현재 사용자의 채팅인지 확인
        val currentUserId = SecurityUtil.Companion.getCurrentUserId()
        if (chat.user.id != currentUserId) {
            throw CustomException(ErrorCode.UNAUTHORIZED_ACCESS, "이 채팅에 접근할 권한이 없습니다")
        }

        return chat
    }

    /**
     * 채팅 삭제
     */
    @Transactional
    fun deleteChat(chatId: Long) {
        val chat = getChatById(chatId) // 권한 체크 포함
        chatRepository.delete(chat)
    }
}