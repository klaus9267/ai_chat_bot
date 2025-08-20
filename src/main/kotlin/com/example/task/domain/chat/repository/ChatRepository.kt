package com.example.task.domain.chat.repository

import com.example.task.domain.chat.entity.Chat
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : JpaRepository<Chat, Long> {

    /**
     * 특정 스레드의 채팅 목록을 최신순으로 조회
     */
    fun findByThreadIdOrderByCreatedAtAsc(threadId: Long): List<Chat>

    /**
     * 특정 스레드의 채팅 목록을 페이징으로 조회
     */
    fun findByThreadIdOrderByCreatedAtDesc(threadId: Long, pageable: Pageable): Page<Chat>

    /**
     * 특정 사용자의 모든 채팅을 최신순으로 조회
     */
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Chat>

    /**
     * 특정 사용자와 스레드의 채팅만 조회
     */
    fun findByUserIdAndThreadIdOrderByCreatedAtAsc(userId: Long, threadId: Long): List<Chat>

    /**
     * 특정 스레드의 채팅 개수 조회
     */
    fun countByThreadId(threadId: Long): Long

    /**
     * 특정 스레드의 최신 N개 채팅 조회 (컨텍스트용)
     */
    @Query("""
        SELECT c FROM Chat c 
        WHERE c.thread.id = :threadId 
        ORDER BY c.createdAt DESC
    """)
    fun findRecentChatsByThreadId(@Param("threadId") threadId: Long, pageable: Pageable): List<Chat>
}