package com.example.task.domain.chat.controller

import com.example.task.domain.chat.service.ChatService
import com.example.task.domain.chat.dto.ChatHistoryResponse
import com.example.task.domain.chat.dto.ChatRequest
import com.example.task.domain.chat.dto.ChatResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chats")
@Tag(name = "채팅", description = "AI 채팅 관리 API")
@SecurityRequirement(name = "bearerAuth")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping
    @Operation(summary = "새 채팅 생성", description = "사용자 메시지를 전송하고 AI 응답을 받습니다")
    fun createChat(@Valid @RequestBody request: ChatRequest): ResponseEntity<ChatResponse> {
        val chat = chatService.createChat(request.message, request.threadId)
        return ResponseEntity.ok(ChatResponse.Companion.from(chat))
    }

    @GetMapping("/thread/{threadId}")
    @Operation(summary = "스레드 채팅 히스토리 조회", description = "특정 스레드의 모든 채팅을 시간순으로 조회합니다")
    fun getChatHistory(
        @Parameter(description = "스레드 ID", example = "1")
        @PathVariable threadId: Long
    ): ResponseEntity<ChatHistoryResponse> {
        val chats = chatService.getChatHistory(threadId)
        val response = ChatHistoryResponse.Companion.from(
            threadId = threadId,
            chats = chats.map { ChatResponse.Companion.from(it) }
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/thread/{threadId}/paged")
    @Operation(summary = "스레드 채팅 히스토리 조회 (페이징)", description = "특정 스레드의 채팅을 페이징으로 조회합니다")
    fun getChatHistoryPaged(
        @Parameter(description = "스레드 ID", example = "1")
        @PathVariable threadId: Long,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Any> {
        val chatPage = chatService.getChatHistory(threadId, pageable)
        val response = mapOf(
            "content" to chatPage.content.map { ChatResponse.Companion.from(it) },
            "totalElements" to chatPage.totalElements,
            "totalPages" to chatPage.totalPages,
            "size" to chatPage.size,
            "number" to chatPage.number,
            "first" to chatPage.isFirst,
            "last" to chatPage.isLast
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/my")
    @Operation(summary = "내 모든 채팅 조회", description = "현재 사용자의 모든 채팅을 최신순으로 조회합니다")
    fun getMyChats(): ResponseEntity<List<ChatResponse>> {
        val chats = chatService.getCurrentUserChats()
        val response = chats.map { ChatResponse.Companion.from(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{chatId}")
    @Operation(summary = "특정 채팅 조회", description = "채팅 ID로 특정 채팅을 조회합니다")
    fun getChat(
        @Parameter(description = "채팅 ID", example = "1")
        @PathVariable chatId: Long
    ): ResponseEntity<ChatResponse> {
        val chat = chatService.getChatById(chatId)
        return ResponseEntity.ok(ChatResponse.Companion.from(chat))
    }

    @DeleteMapping("/{chatId}")
    @Operation(summary = "채팅 삭제", description = "특정 채팅을 삭제합니다")
    fun deleteChat(
        @Parameter(description = "채팅 ID", example = "1")
        @PathVariable chatId: Long
    ): ResponseEntity<Void> {
        chatService.deleteChat(chatId)
        return ResponseEntity.noContent().build()
    }
}