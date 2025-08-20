package com.example.task.domain.thread.controller

import com.example.task.domain.thread.service.ThreadService
import com.example.task.domain.thread.dto.ThreadResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/threads")
@Tag(name = "스레드", description = "스레드 관리 API")
@SecurityRequirement(name = "bearerAuth")
class ThreadController(
    private val threadService: ThreadService
) {

    @GetMapping
    @Operation(summary = "사용자 스레드 조회", description = "인증된 사용자의 모든 스레드를 조회합니다")
    fun getUserThreads(): ResponseEntity<List<ThreadResponse>> {
        val threads = threadService.getCurrentUserThreads()
        val response = threads.map { ThreadResponse.Companion.from(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{threadId}")
    @Operation(summary = "ID로 스레드 조회", description = "ID를 통해 특정 스레드를 조회합니다")
    fun getThread(
        @Parameter(description = "스레드 ID", example = "1")
        @PathVariable threadId: Long
    ): ResponseEntity<ThreadResponse> {
        val thread = threadService.getCurrentUserThread(threadId)
        return ResponseEntity.ok(ThreadResponse.Companion.from(thread))
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

    @PostMapping("/active")
    @Operation(summary = "활성 스레드 조회/생성", description = "현재 활성 스레드를 조회하거나 만료된 경우 새 스레드를 생성합니다")
    fun getOrCreateActiveThread(): ResponseEntity<ThreadResponse> {
        val thread = threadService.getCurrentUserActiveThread()
        return ResponseEntity.ok(ThreadResponse.Companion.from(thread))
    }

}