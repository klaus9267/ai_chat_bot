package com.example.task.domain.thread.repository

import com.example.task.domain.thread.entity.Thread
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface ThreadRepository : JpaRepository<Thread, Long> {

    fun findByUserId(userId: Long): List<Thread>

    @Query("SELECT t FROM Thread t WHERE t.user.id = :userId ORDER BY t.updatedAt DESC")
    fun findByUserIdOrderByUpdatedAtDesc(@Param("userId") userId: Long): List<Thread>
    
    /**
     * 페이지네이션을 지원하는 사용자별 스레드 조회
     */
    @Query("SELECT t FROM Thread t WHERE t.user.id = :userId")
    fun findByUserIdOrderByUpdatedAtDesc(@Param("userId") userId: Long, pageable: Pageable): Page<Thread>

    @Query("SELECT t FROM Thread t WHERE t.user.id = :userId AND t.updatedAt >= :cutoffTime ORDER BY t.updatedAt DESC")
    fun findActiveThreadByUserId(
        @Param("userId") userId: Long,
        @Param("cutoffTime") cutoffTime: LocalDateTime
    ): Optional<Thread>

    fun findByIdAndUserId(id: Long, userId: Long): Optional<Thread>
}