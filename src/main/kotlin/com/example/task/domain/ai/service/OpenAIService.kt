package com.example.task.domain.ai.service

import com.example.task.application.handler.exception.CustomException
import com.example.task.application.handler.exception.ErrorCode
import com.example.task.application.config.OpenAIProperties
import com.example.task.domain.ai.dto.OpenAIMessage
import com.example.task.domain.ai.dto.OpenAIRequest
import com.example.task.domain.ai.dto.OpenAIResponse
import com.example.task.domain.chat.entity.Chat
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class OpenAIService(
    private val openAIProperties: OpenAIProperties,
    @Qualifier("openAIRestClient") private val restClient: RestClient
) {

    private val logger = LoggerFactory.getLogger(OpenAIService::class.java)

    companion object {
        const val SYSTEM_MESSAGE = "당신은 도움이 되는 AI 어시스턴트입니다. 사용자의 질문에 정확하고 친절하게 답변해주세요. 한국어로 답변해주시기 바랍니다."
    }

    /**
     * OpenAI API를 통한 AI 응답 생성
     */
    fun generateResponse(message: String, chatHistory: List<Chat> = emptyList()): String {
        try {
            // 메시지 구성
            val messages = mutableListOf<OpenAIMessage>()

            // 시스템 메시지 추가
            messages.add(OpenAIMessage("system", SYSTEM_MESSAGE))

            // 이전 대화 컨텍스트 추가 (시간 순서대로)
            chatHistory.forEach { chat ->
                messages.add(OpenAIMessage("user", chat.question))
                messages.add(OpenAIMessage("assistant", chat.answer))
            }

            // 현재 사용자 메시지 추가
            messages.add(OpenAIMessage("user", message))

            // OpenAI 요청 생성
            val request = OpenAIRequest(
                model = openAIProperties.model,
                messages = messages,
                maxTokens = openAIProperties.maxTokens,
                temperature = openAIProperties.temperature
            )

            // API 호출
            val response = restClient
                .post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(OpenAIResponse::class.java)
                ?: throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "OpenAI API 응답을 받을 수 없습니다")

            // 응답 추출
            val aiMessage = response.choices.firstOrNull()?.message?.content
                ?: throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "AI 응답이 비어있습니다")

            logger.info("OpenAI API 호출 성공 - 토큰 사용량: ${response.usage?.totalTokens ?: "알 수 없음"}")

            return aiMessage

        } catch (e: CustomException) {
            throw e
        } catch (e: org.springframework.web.client.HttpClientErrorException) {
            logger.error("OpenAI API 클라이언트 오류: ${e.statusCode} - ${e.responseBodyAsString}", e)
            when (e.statusCode.value()) {
                429 -> throw CustomException(ErrorCode.OPENAI_RATE_LIMIT, "OpenAI API 요청 제한에 도달했습니다")
                else -> throw CustomException(ErrorCode.OPENAI_API_ERROR, "OpenAI API 오류: ${e.message}")
            }
        } catch (e: org.springframework.web.client.HttpServerErrorException) {
            logger.error("OpenAI API 서버 오류: ${e.statusCode} - ${e.responseBodyAsString}", e)
            throw CustomException(ErrorCode.OPENAI_API_ERROR, "OpenAI API 서버 오류: ${e.message}")
        } catch (e: Exception) {
            logger.error("OpenAI API 호출 실패", e)
            throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "AI 응답 생성에 실패했습니다: ${e.message}")
        }
    }
}