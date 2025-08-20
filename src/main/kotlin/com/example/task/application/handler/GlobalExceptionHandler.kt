package com.example.task.application.handler

import com.example.task.application.handler.exception.CustomException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Custom exception occurred: ${ex.message}")

        val errorResponse = ErrorResponse(
            status = ex.status.value(),
            code = ex.code,
            message = ex.message,
            path = getPath(request)
        )

        return ResponseEntity(errorResponse, ex.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Validation exception occurred: ${ex.message}")

        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            FieldError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "Invalid value"
            )
        }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "VALIDATION_ERROR",
            message = "Input validation failed",
            path = getPath(request),
            fieldErrors = fieldErrors
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        ex: RuntimeException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Runtime exception occurred: ${ex.message}")

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "BAD_REQUEST",
            message = ex.message ?: "Bad request",
            path = getPath(request)
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            code = "INTERNAL_SERVER_ERROR",
            message = "An unexpected error occurred",
            path = getPath(request)
        )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun getPath(request: WebRequest): String {
        return request.getDescription(false).removePrefix("uri=")
    }
}