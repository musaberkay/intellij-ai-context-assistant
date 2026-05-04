package com.mbk.aiassistant.provider

import com.mbk.aiassistant.chat.ChatMessage
import com.mbk.aiassistant.chat.Role
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OpenAiProviderTest {

    private val provider = OpenAiProvider("http://localhost:11434", "")

    @Test
    fun `buildBody includes model and messages`() {
        val messages = listOf(
            ChatMessage(Role.USER, "hello"),
            ChatMessage(Role.ASSISTANT, "hi there")
        )
        val body = provider.buildBody(messages, "llama3.2")
        assertTrue(body.contains(""""model":"llama3.2""""))
        assertTrue(body.contains(""""role":"user""""))
        assertTrue(body.contains(""""role":"assistant""""))
        assertTrue(body.contains(""""content":"hello""""))
        assertTrue(body.contains(""""content":"hi there""""))
    }

    @Test
    fun `buildBody escapes newlines in content`() {
        val messages = listOf(ChatMessage(Role.USER, "line1\nline2"))
        val body = provider.buildBody(messages, "llama3.2")
        assertTrue(body.contains("line1\\nline2"))
    }

    @Test
    fun `parseContent extracts assistant reply`() {
        val json = """{"choices":[{"message":{"role":"assistant","content":"Hello world"}}]}"""
        assertEquals("Hello world", provider.parseContent(json))
    }

    @Test
    fun `parseContent unescapes newlines`() {
        val json = """{"choices":[{"message":{"role":"assistant","content":"line1\nline2"}}]}"""
        assertEquals("line1\nline2", provider.parseContent(json))
    }

    @Test
    fun `parseContent throws on unexpected format`() {
        assertThrows(IllegalStateException::class.java) {
            provider.parseContent("""{"error":"not found"}""")
        }
    }
}
