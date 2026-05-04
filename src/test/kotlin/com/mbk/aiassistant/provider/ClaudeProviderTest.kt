package com.mbk.aiassistant.provider

import com.mbk.aiassistant.chat.ChatMessage
import com.mbk.aiassistant.chat.Role
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClaudeProviderTest {

    private val provider = ClaudeProvider("test-key")

    @Test
    fun `buildBody includes model and max_tokens`() {
        val messages = listOf(ChatMessage(Role.USER, "hi"))
        val body = provider.buildBody(messages, "claude-haiku-4-5-20251001")
        assertTrue(body.contains(""""model":"claude-haiku-4-5-20251001""""))
        assertTrue(body.contains("max_tokens"))
    }

    @Test
    fun `buildBody maps USER to user role`() {
        val messages = listOf(ChatMessage(Role.USER, "hello"))
        val body = provider.buildBody(messages, "claude-haiku-4-5-20251001")
        assertTrue(body.contains(""""role":"user""""))
        assertTrue(body.contains(""""content":"hello""""))
    }

    @Test
    fun `buildBody maps ASSISTANT to assistant role`() {
        val messages = listOf(ChatMessage(Role.ASSISTANT, "hi there"))
        val body = provider.buildBody(messages, "claude-haiku-4-5-20251001")
        assertTrue(body.contains(""""role":"assistant""""))
    }

    @Test
    fun `parseText extracts reply from Anthropic response`() {
        val json = """{"content":[{"type":"text","text":"Hello world"}]}"""
        assertEquals("Hello world", provider.parseText(json))
    }

    @Test
    fun `parseText unescapes newlines`() {
        val json = """{"content":[{"type":"text","text":"line1\nline2"}]}"""
        assertEquals("line1\nline2", provider.parseText(json))
    }

    @Test
    fun `parseText throws on unexpected format`() {
        assertThrows(IllegalStateException::class.java) {
            provider.parseText("""{"error":"invalid_api_key"}""")
        }
    }
}
