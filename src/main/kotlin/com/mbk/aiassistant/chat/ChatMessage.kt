package com.mbk.aiassistant.chat

enum class Role { USER, ASSISTANT }

data class ChatMessage(
    val role: Role,
    val content: String,
    val timestamp: Long = System.currentTimeMillis() / 1000
)
