package com.mbk.aiassistant.settings

import com.intellij.openapi.options.Configurable
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField

class PluginSettingsConfigurable : Configurable {

    private var apiKeyField: JPasswordField? = null

    override fun getDisplayName() = "AI Context Assistant"

    override fun createComponent(): JComponent {
        val field = JPasswordField(40)
        apiKeyField = field

        return JPanel(GridBagLayout()).apply {
            val gbc = GridBagConstraints().apply { insets = Insets(4, 4, 4, 4) }

            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST
            add(JLabel("Claude API Key:"), gbc)

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0
            add(field, gbc)

            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE
            gbc.weightx = 0.0
            add(JLabel("<html><small>Get your key at <a href='https://console.anthropic.com'>console.anthropic.com</a></small></html>"), gbc)
        }
    }

    override fun isModified(): Boolean {
        val entered = String(apiKeyField?.password ?: charArrayOf())
        return entered != PluginSettings.getInstance().apiKey
    }

    override fun apply() {
        PluginSettings.getInstance().apiKey = String(apiKeyField?.password ?: charArrayOf())
    }

    override fun reset() {
        apiKeyField?.text = PluginSettings.getInstance().apiKey
    }
}
