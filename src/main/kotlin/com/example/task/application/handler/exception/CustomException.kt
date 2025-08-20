package com.example.task.application.handler.exception

class CustomException(
    val errorCode: ErrorCode,
    private val detail: String? = null
) : RuntimeException(detail ?: errorCode.message) {
    
    val code: String = errorCode.code
    val status = errorCode.status
    
    override val message: String
        get() = detail ?: errorCode.message
}