package com.example.task.application

import com.example.task.common.exception.CustomException
import com.example.task.common.exception.ErrorCode
import com.example.task.config.OpenAIProperties
import com.example.task.config.SecurityUtil
import com.example.task.domain.chat.Chat
import com.example.task.domain.chat.ChatRepository
import com.example.task.domain.thread.Thread
import com.example.task.dto.openai.OpenAIMessage
import com.example.task.dto.openai.OpenAIRequest
import com.example.task.dto.openai.OpenAIResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRepository: ChatRepository,
    private val threadService: ThreadService,
    private val userService: UserService,
    private val openAIProperties: OpenAIProperties,
    @Qualifier("openAIWebClient") private val webClient: WebClient
) {
    
    private val logger = LoggerFactory.getLogger(ChatService::class.java)
    
    companion object {
        const val SYSTEM_MESSAGE = """
            당신은 도움이 되는 AI 어시스턴트입니다. 
            사용자의 질문에 정확하고 친절하게 답변해주세요.
            한국어로 답변해주시기 바랍니다.
        """.trimIndent()
        
        const val CONTEXT_LIMIT = 10 // 이전 대화 컨텍스트 개수
    }
    
    /**
     * 새로운 채팅 생성 및 AI 응답 생성
     */
    @Transactional
    fun createChat(message: String, threadId: Long?): Chat {
        val userInfo = SecurityUtil.getCurrentUserInfo()
        val user = userService.findById(userInfo.userId)
            ?: throw CustomException(ErrorCode.USER_NOT_FOUND, "User not found: ${userInfo.userId}")
        
        // 스레드 가져오기 또는 생성
        val thread = if (threadId != null) {
            threadService.getCurrentUserThread(threadId)
        } else {
            threadService.getCurrentUserActiveThread()
        }
        
        // AI 응답 생성
        val aiResponse = generateAIResponse(message, thread)
        
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
     * OpenAI API를 통한 AI 응답 생성
     */
    private fun generateAIResponse(message: String, thread: Thread): String {
        try {
            // 이전 대화 컨텍스트 가져오기
            val recentChats = chatRepository.findRecentChatsByThreadId(
                thread.id, 
                PageRequest.of(0, CONTEXT_LIMIT)
            )
            
            // 메시지 구성
            val messages = mutableListOf<OpenAIMessage>()
            
            // 시스템 메시지 추가
            messages.add(OpenAIMessage("system", SYSTEM_MESSAGE))
            
            // 이전 대화 컨텍스트 추가 (시간 순서대로)
            recentChats.reversed().forEach { chat ->
                messages.add(OpenAIMessage("user", chat.question))
                messages.add(OpenAIMessage("assistant", chat.answer))
            }
            
            // 현재 사용자 메시지 추가
            messages.add(OpenAIMessage("user", message))
            
            // OpenAI 요청 생성
            val request = OpenAIRequest(
                model = openAIProperties.model,
                messages = messages,
                maxTokens = openAIProperties.maxTokens,
                temperature = openAIProperties.temperature
            )
            
            // API 호출
            val response = webClient
                .post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAIResponse::class.java)
                .block()
                ?: throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "OpenAI API 응답을 받을 수 없습니다")
            
            // 응답 추출
            val aiMessage = response.choices.firstOrNull()?.message?.content
                ?: throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "AI 응답이 비어있습니다")
            
            logger.info("OpenAI API 호출 성공 - 토큰 사용량: ${response.usage?.totalTokens ?: "알 수 없음"}")
            
            return aiMessage
            
        } catch (e: CustomException) {
            throw e
        } catch (e: Exception) {
            logger.error("OpenAI API 호출 실패", e)
            throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "AI 응답 생성에 실패했습니다: ${e.message}")
        }
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
        val userId = SecurityUtil.getCurrentUserId()
        return chatRepository.findByUserIdOrderByCreatedAtDesc(userId)
    }
    
    /**
     * 특정 채팅 조회
     */
    fun getChatById(chatId: Long): Chat {
        val chat = chatRepository.findById(chatId)
            .orElseThrow { CustomException(ErrorCode.NOT_FOUND, "채팅을 찾을 수 없습니다: $chatId") }
        
        // 권한 체크: 현재 사용자의 채팅인지 확인
        val currentUserId = SecurityUtil.getCurrentUserId()
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