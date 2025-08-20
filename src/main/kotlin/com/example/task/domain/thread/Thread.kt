package com.example.task.domain.thread

import com.example.task.common.BaseEntity
import com.example.task.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "threads")
class Thread(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}