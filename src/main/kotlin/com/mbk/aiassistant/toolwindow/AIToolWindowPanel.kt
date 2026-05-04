package com.mbk.aiassistant.toolwindow

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.JPanel

class AIToolWindowPanel : JPanel(BorderLayout()) {

    private val textArea = JBTextArea(
        "Select code in the editor and press Ctrl+Alt+A\n(or right-click → Ask AI about This)."
    ).apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        font = Font(Font.MONOSPACED, Font.PLAIN, 13)
    }

    init {
        add(JBScrollPane(textArea), BorderLayout.CENTER)
    }

    fun updateContent(text: String) {
        textArea.text = text
        textArea.caretPosition = 0
    }
}
