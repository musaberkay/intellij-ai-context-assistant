package com.mbk.aiassistant.chat

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.io.File
import java.nio.file.Paths

@Service(Service.Level.PROJECT)
class ChatSessionManager(private val project: Project) {

    private val gson = Gson()
    private val session = ChatSession()
    private var loaded = false

    fun getMessages(): List<ChatMessage> {
        ensureLoaded()
        return session.messages
    }

    fun addMessage(message: ChatMessage) {
        ensureLoaded()
        session.add(message)
        saveToDisk()
    }

    fun clearSession() {
        session.clear()
        loaded = true
        historyFile().delete()
    }

    private fun ensureLoaded() {
        if (loaded) return
        loaded = true
        val file = historyFile()
        if (!file.exists()) return
        runCatching {
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            val messages: List<ChatMessage> = gson.fromJson(file.readText(), type)
            messages.forEach { session.add(it) }
        }
    }

    private fun saveToDisk() {
        val file = historyFile()
        file.parentFile.mkdirs()
        file.writeText(gson.toJson(session.messages))
    }

    private fun historyFile(): File =
        Paths.get(
            project.basePath ?: System.getProperty("user.home"),
            ".idea", "ai-assistant", "history.json"
        ).toFile()

    companion object {
        fun getInstance(project: Project): ChatSessionManager = project.service()
    }
}
