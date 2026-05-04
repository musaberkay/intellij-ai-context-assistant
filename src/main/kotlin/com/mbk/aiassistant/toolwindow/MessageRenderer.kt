package com.mbk.aiassistant.toolwindow

import com.mbk.aiassistant.chat.ChatMessage
import com.mbk.aiassistant.chat.Role
import java.awt.Color
import java.awt.Component
import javax.swing.JList
import javax.swing.JTextArea
import javax.swing.ListCellRenderer
import javax.swing.border.EmptyBorder

class MessageRenderer : ListCellRenderer<ChatMessage> {

    override fun getListCellRendererComponent(
        list: JList<out ChatMessage>,
        value: ChatMessage,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component = JTextArea(value.content).apply {
        lineWrap = true
        wrapStyleWord = true
        isOpaque = true
        font = list.font
        foreground = list.foreground
        border = if (value.role == Role.USER)
            EmptyBorder(6, 8, 6, 8)
        else
            EmptyBorder(6, 24, 6, 8)
        background = if (value.role == Role.ASSISTANT)
            Color(245, 245, 250)
        else
            list.background
        // Force correct wrap width before the list queries preferredSize
        setSize(list.width.coerceAtLeast(100), Short.MAX_VALUE.toInt())
    }
}
