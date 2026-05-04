package com.mbk.aiassistant.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.mbk.aiassistant.api.ClaudeClient
import com.mbk.aiassistant.settings.PluginSettings
import com.mbk.aiassistant.toolwindow.AIToolWindowPanel

class AskAIAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText?.takeIf { it.isNotBlank() } ?: return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        val apiKey = PluginSettings.getInstance().apiKey

        if (apiKey.isBlank()) {
            updatePanel(project, "API key not configured.\n\nGo to Settings → Tools → AI Context Assistant and enter your Claude API key.")
            return
        }

        updatePanel(project, "Asking Claude AI...")

        val prompt = buildPrompt(
            code = selectedText,
            fileName = file?.name,
            language = file?.extension,
            projectName = project.name
        )

        ApplicationManager.getApplication().executeOnPooledThread {
            val result = try {
                ClaudeClient.getInstance().ask(prompt, apiKey)
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
            ApplicationManager.getApplication().invokeLater {
                updatePanel(project, result)
            }
        }
    }

    override fun update(event: AnActionEvent) {
        val hasSelection = event.getData(CommonDataKeys.EDITOR)
            ?.selectionModel?.hasSelection() == true
        event.presentation.isEnabledAndVisible = hasSelection
    }

    // BGT so the update check doesn't block the EDT
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    private fun buildPrompt(code: String, fileName: String?, language: String?, projectName: String) =
        buildString {
            appendLine("You are an AI assistant embedded in IntelliJ IDEA. Explain the following code clearly and concisely.")
            appendLine()
            fileName?.let { appendLine("File: $it") }
            language?.let { appendLine("Language: $it") }
            appendLine("Project: $projectName")
            appendLine()
            appendLine("```")
            appendLine(code)
            appendLine("```")
            appendLine()
            append("Focus on what the code does, why it is structured this way, and any important patterns or non-obvious behaviours.")
        }

    private fun updatePanel(project: Project, message: String) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("AI Assistant") ?: return
        toolWindow.show()
        val panel = toolWindow.contentManager.getContent(0)?.component as? AIToolWindowPanel ?: return
        panel.updateContent(message)
    }
}
