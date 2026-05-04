package com.mbk.aiassistant.provider

import com.mbk.aiassistant.chat.ChatMessage

interface LlmProvider {
    fun send(messages: List<ChatMessage>, model: String): String
}
