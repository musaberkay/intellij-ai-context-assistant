package com.mbk.aiassistant.chat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ChatMessageTest {

    @Test
    fun `role and content are stored correctly`() {
        val msg = ChatMessage(Role.USER, "hello")
        assertEquals(Role.USER, msg.role)
        assertEquals("hello", msg.content)
    }

    @Test
    fun `timestamp defaults to current epoch second`() {
        val before = System.currentTimeMillis() / 1000
        val msg = ChatMessage(Role.ASSISTANT, "hi")
        val after = System.currentTimeMillis() / 1000
        assertTrue(msg.timestamp in before..after)
    }
}
