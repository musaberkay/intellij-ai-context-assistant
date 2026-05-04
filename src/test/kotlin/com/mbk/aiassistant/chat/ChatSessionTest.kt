package com.mbk.aiassistant.chat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ChatSessionTest {

    @Test
    fun `messages starts empty`() {
        assertTrue(ChatSession().messages.isEmpty())
    }

    @Test
    fun `add appends a message`() {
        val session = ChatSession()
        val msg = ChatMessage(Role.USER, "hi")
        session.add(msg)
        assertEquals(listOf(msg), session.messages)
    }

    @Test
    fun `clear empties the list`() {
        val session = ChatSession()
        session.add(ChatMessage(Role.USER, "hi"))
        session.clear()
        assertTrue(session.messages.isEmpty())
    }

    @Test
    fun `messages returns a snapshot not a live view`() {
        val session = ChatSession()
        val snapshot = session.messages
        session.add(ChatMessage(Role.USER, "hi"))
        assertTrue(snapshot.isEmpty())
    }
}
