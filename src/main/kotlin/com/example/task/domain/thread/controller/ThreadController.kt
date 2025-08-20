package com.example.task.domain.thread.controller

import com.example.task.domain.chat.dto.ChatResponse
import com.example.task.domain.chat.service.ChatService
import com.example.task.domain.thread.dto.ThreadDetailResponse
import com.example.task.domain.thread.dto.ThreadResponse
import com.example.task.domain.thread.service.ThreadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/threads")
@Tag(name = "스레드", description = "스레드 관리 API")
@SecurityRequirement(name = "bearerAuth")
class ThreadController(
    private val threadService: ThreadService,
    private val chatService: ChatService
) {

    @GetMapping
    @Operation(
        summary = "대화 목록 조회", 
        description = "스레드 단위로 그룹화된 대화 목록을 조회합니다. userId가 없으면 현재 사용자의 대화를, 있으면 해당 사용자의 대화를 조회합니다(권한에 따라). 각 사용자는 자신의 대화만, 관리자는 모든 대화를 조회할 수 있습니다. 생성일시 기준으로 정렬 및 페이지네이션을 지원합니다."
    )
    fun getUserThreads(
        @Parameter(description = "조회할 사용자 ID (선택사항, 없으면 현재 사용자)", example = "1")
        @RequestParam(required = false) userId: Long?,
        @ParameterObject pageable: Pageable
    ): ResponseEntity<Page<ThreadDetailResponse>> {
        val threadsPage = threadService.getUserThreads(userId, pageable)
        val response = threadsPage.map {
            val chats = chatService.getChatHistory(it.id,pageable)
            val chatResponses = chats.map { ChatResponse.Companion.from(it) }
            ThreadDetailResponse.from(it, chatResponses.content)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{threadId}")
    @Operation(summary = "특정 스레드 조회", description = "특정 스레드의 상세 정보와 포함된 모든 채팅을 조회합니다")
    fun getThread(
        @Parameter(description = "스레드 ID", example = "1")
        @PathVariable threadId: Long
    ): ResponseEntity<ThreadDetailResponse> {
        val thread = threadService.getCurrentUserThread(threadId)
        val chats = chatService.getChatHistory(threadId)
        val chatResponses = chats.map { ChatResponse.Companion.from(it) }
        val response = ThreadDetailResponse.from(thread, chatResponses)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{threadId}")
    @Operation(summary = "스레드 삭제", description = "특정 스레드를 삭제합니다")
    fun deleteThread(
        @Parameter(description = "스레드 ID", example = "1")
        @PathVariable threadId: Long
    ): ResponseEntity<Void> {
        threadService.deleteCurrentUserThread(threadId)
        return ResponseEntity.noContent().build()
    }
}