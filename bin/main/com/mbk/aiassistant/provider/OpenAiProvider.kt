package com.mbk.aiassistant.provider

import com.mbk.aiassistant.chat.ChatMessage
import com.mbk.aiassistant.chat.Role
import com.mbk.aiassistant.util.escapeJson
import com.mbk.aiassistant.util.unescapeJson
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class OpenAiProvider(
    private val baseUrl: String,
    private val apiKey: String
) : LlmProvider {

    private val http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    override fun send(messages: List<ChatMessage>, model: String): String {
        val effectiveKey = apiKey.ifBlank { "ollama" }
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/v1/chat/completions"))
            .timeout(Duration.ofSeconds(60))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $effectiveKey")
            .POST(HttpRequest.BodyPublishers.ofString(buildBody(messages, model)))
            .build()

        val response = http.send(request, HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() == 200) { "HTTP ${response.statusCode()}: ${response.body()}" }
        return parseContent(response.body())
    }

    internal fun buildBody(messages: List<ChatMessage>, model: String): String {
        val msgs = messages.joinToString(",") { msg ->
            val role = if (msg.role == Role.USER) "user" else "assistant"
            """{"role":"$role","content":"${msg.content.escapeJson()}"}"""
        }
        return """{"model":"$model","messages":[$msgs]}"""
    }

    internal fun parseContent(json: String): String {
        val match = """"content"\s*:\s*"((?:[^"\\]|\\.)*)"""".toRegex().find(json)
            ?: error("Unexpected response format from OpenAI-compatible API")
        return match.groupValues[1].unescapeJson()
    }
}
