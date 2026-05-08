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

class ClaudeProvider(private val apiKey: String) : LlmProvider {

    private val http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    override fun send(messages: List<ChatMessage>, model: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.anthropic.com/v1/messages"))
            .timeout(Duration.ofSeconds(60))
            .header("Content-Type", "application/json")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .POST(HttpRequest.BodyPublishers.ofString(buildBody(messages, model)))
            .build()

        val response = http.send(request, HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() == 200) { "HTTP ${response.statusCode()}: ${response.body()}" }
        return parseText(response.body())
    }

    internal fun buildBody(messages: List<ChatMessage>, model: String): String {
        val msgs = messages.joinToString(",") { msg ->
            val role = if (msg.role == Role.USER) "user" else "assistant"
            """{"role":"$role","content":"${msg.content.escapeJson()}"}"""
        }
        return """{"model":"$model","max_tokens":2048,"messages":[$msgs]}"""
    }

    internal fun parseText(json: String): String {
        val match = """"text"\s*:\s*"((?:[^"\\]|\\.)*)"""".toRegex().find(json)
            ?: error("Unexpected response format from Claude API")
        return match.groupValues[1].unescapeJson()
    }
}
