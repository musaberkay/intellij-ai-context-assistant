package com.mbk.aiassistant.chat

class ChatSession {
    private val _messages = mutableListOf<ChatMessage>()

    val messages: List<ChatMessage> get() = _messages.toList()

    fun add(message: ChatMessage) {
        _messages.add(message)
    }

    fun clear() {
        _messages.clear()
    }
}
