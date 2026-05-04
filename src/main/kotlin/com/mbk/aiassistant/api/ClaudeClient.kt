package com.mbk.aiassistant.api

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Service
class ClaudeClient {

    private val http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    fun ask(prompt: String, apiKey: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.anthropic.com/v1/messages"))
            .timeout(Duration.ofSeconds(60))
            .header("Content-Type", "application/json")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .POST(HttpRequest.BodyPublishers.ofString(buildBody(prompt)))
            .build()

        val response = http.send(request, HttpResponse.BodyHandlers.ofString())

        check(response.statusCode() == 200) {
            "Claude API error ${response.statusCode()}: ${response.body()}"
        }

        return extractText(response.body())
    }

    private fun buildBody(prompt: String): String {
        val escaped = prompt
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return """{"model":"claude-haiku-4-5-20251001","max_tokens":1024,"messages":[{"role":"user","content":"$escaped"}]}"""
    }

    // Extracts the first "text" field value from the Anthropic Messages API response.
    // The response shape is stable: content[0].text always carries the assistant reply.
    private fun extractText(json: String): String {
        val match = """"text"\s*:\s*"((?:[^"\\]|\\.)*)"""".toRegex().find(json)
            ?: error("Unexpected response format from Claude API")
        return match.groupValues[1]
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
    }

    companion object {
        fun getInstance(): ClaudeClient = service()
    }
}
