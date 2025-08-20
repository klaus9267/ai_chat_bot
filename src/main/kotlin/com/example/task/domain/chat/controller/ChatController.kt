package com.example.task.domain.chat.controller

import com.example.task.domain.chat.dto.ChatRequest
import com.example.task.domain.chat.dto.ChatResponse
import com.example.task.domain.chat.service.ChatService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        val chat = chatService.createChat(request.message)
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