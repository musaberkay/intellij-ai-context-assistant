package com.mbk.aiassistant.toolwindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.mbk.aiassistant.chat.ChatMessage
import com.mbk.aiassistant.chat.ChatSessionManager
import com.mbk.aiassistant.chat.Role
import com.mbk.aiassistant.provider.ClaudeProvider
import com.mbk.aiassistant.provider.OpenAiProvider
import com.mbk.aiassistant.settings.PluginSettings
import com.mbk.aiassistant.settings.ProviderType
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*

class ChatPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val listModel = DefaultListModel<ChatMessage>()
    private val messageList = JList(listModel).apply {
        cellRenderer = MessageRenderer()
        layoutOrientation = JList.VERTICAL
    }
    private val inputField = JTextField().apply { toolTipText = "Type a message…" }
    private val sendButton = JButton("Send")
    private val modelCombo = JComboBox<String>().apply { isEditable = true }
    private val clearButton = JButton("Clear")
    private val manager = ChatSessionManager.getInstance(project)

    init {
        val settings = PluginSettings.getInstance()
        modelCombo.addItem(settings.defaultModel)
        modelCombo.selectedItem = settings.defaultModel

        add(buildHeader(), BorderLayout.NORTH)
        add(JBScrollPane(messageList), BorderLayout.CENTER)
        add(buildInputRow(), BorderLayout.SOUTH)

        manager.getMessages().forEach { listModel.addElement(it) }
        scrollToBottom()

        sendButton.addActionListener { send() }
        inputField.addActionListener { send() }
        clearButton.addActionListener {
            manager.clearSession()
            listModel.clear()
        }
    }

    fun appendUserMessage(text: String) {
        inputField.text = text
        inputField.requestFocusInWindow()
    }

    private fun send() {
        val text = inputField.text.trim().takeIf { it.isNotBlank() } ?: return
        inputField.text = ""

        val userMsg = ChatMessage(Role.USER, text)
        manager.addMessage(userMsg)
        listModel.addElement(userMsg)

        val placeholder = ChatMessage(Role.ASSISTANT, "Thinking…")
        listModel.addElement(placeholder)
        scrollToBottom()

        val model = modelCombo.selectedItem?.toString()
            ?: PluginSettings.getInstance().defaultModel
        val messagesToSend = manager.getMessages().toList()

        ApplicationManager.getApplication().executeOnPooledThread {
            val result = runCatching { buildProvider().send(messagesToSend, model) }
                .getOrElse { "Error: ${it.message}" }
            val responseMsg = ChatMessage(Role.ASSISTANT, result)
            manager.addMessage(responseMsg)
            ApplicationManager.getApplication().invokeLater {
                listModel.set(listModel.size - 1, responseMsg)
                scrollToBottom()
            }
        }
    }

    private fun buildProvider() = PluginSettings.getInstance().let { s ->
        when (s.providerType) {
            ProviderType.CLAUDE -> ClaudeProvider(s.apiKey)
            ProviderType.OPENAI_COMPATIBLE -> OpenAiProvider(s.baseUrl, s.apiKey)
        }
    }

    private fun buildHeader() = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
        add(JLabel("Model:"))
        add(modelCombo)
        add(clearButton)
    }

    private fun buildInputRow() = JPanel(BorderLayout()).apply {
        add(inputField, BorderLayout.CENTER)
        add(sendButton, BorderLayout.EAST)
    }

    private fun scrollToBottom() {
        if (listModel.size > 0) messageList.ensureIndexIsVisible(listModel.size - 1)
    }
}
