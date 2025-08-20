package com.example.task.domain.chat

import com.example.task.common.BaseEntity
import com.example.task.domain.thread.Thread
import com.example.task.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "chats")
class Chat(
    @Column(nullable = false, columnDefinition = "TEXT")
    var question: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var answer: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    var thread: Thread
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}