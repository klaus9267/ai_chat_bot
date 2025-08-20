package com.example.task.domain.feedback

import com.example.task.domain.common.BaseEntity
import com.example.task.domain.chat.entity.Chat
import com.example.task.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "feedbacks")
class Feedback(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    var chat: Chat,

    @Column(nullable = false, name = "is_positive")
    var isPositive: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: FeedbackStatus = FeedbackStatus.PENDING
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}