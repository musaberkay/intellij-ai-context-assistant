package com.mbk.aiassistant.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.mbk.aiassistant.toolwindow.ChatPanel

class AskAIAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText?.takeIf { it.isNotBlank() } ?: return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)

        val message = buildMessage(
            code = selectedText,
            fileName = file?.name,
            language = file?.extension,
            projectName = project.name
        )

        getChatPanel(project)?.appendUserMessage(message)
    }

    override fun update(event: AnActionEvent) {
        val hasSelection = event.getData(CommonDataKeys.EDITOR)
            ?.selectionModel?.hasSelection() == true
        event.presentation.isEnabledAndVisible = hasSelection
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    private fun buildMessage(code: String, fileName: String?, language: String?, projectName: String) =
        buildString {
            fileName?.let { appendLine("File: $it") }
            language?.let { appendLine("Language: $it") }
            appendLine("Project: $projectName")
            appendLine()
            appendLine("```${language ?: ""}")
            appendLine(code)
            append("```")
        }

    private fun getChatPanel(project: Project): ChatPanel? {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("AI Assistant") ?: return null
        toolWindow.show()
        return toolWindow.contentManager.getContent(0)?.component as? ChatPanel
    }
}
