package com.mbk.aiassistant.settings

import com.intellij.openapi.options.Configurable
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class PluginSettingsConfigurable : Configurable {

    private var claudeRadio: JRadioButton? = null
    private var openAiRadio: JRadioButton? = null
    private var baseUrlField: JTextField? = null
    private var defaultModelField: JTextField? = null
    private var apiKeyField: JPasswordField? = null

    override fun getDisplayName() = "AI Context Assistant"

    override fun createComponent(): JComponent {
        claudeRadio = JRadioButton("Claude API (api.anthropic.com)")
        openAiRadio = JRadioButton("OpenAI-compatible endpoint (Ollama, LM Studio, OpenAI, Groq…)")
        ButtonGroup().apply { add(claudeRadio); add(openAiRadio) }

        baseUrlField = JTextField(30)
        defaultModelField = JTextField(30)
        apiKeyField = JPasswordField(30)

        val toggleBaseUrl = { baseUrlField?.isVisible = openAiRadio?.isSelected == true }
        claudeRadio?.addActionListener { toggleBaseUrl() }
        openAiRadio?.addActionListener { toggleBaseUrl() }

        return JPanel(GridBagLayout()).apply {
            fun gbc(x: Int, y: Int, fill: Int = GridBagConstraints.NONE, weightx: Double = 0.0, gridwidth: Int = 1) =
                GridBagConstraints().apply {
                    gridx = x; gridy = y; this.fill = fill; this.weightx = weightx
                    this.gridwidth = gridwidth; anchor = GridBagConstraints.WEST
                    insets = Insets(4, 4, 4, 4)
                }

            add(JLabel("Provider:"), gbc(0, 0, gridwidth = 2))
            add(claudeRadio!!, gbc(0, 1, gridwidth = 2).also { it.insets = Insets(0, 20, 2, 4) })
            add(openAiRadio!!, gbc(0, 2, gridwidth = 2).also { it.insets = Insets(0, 20, 4, 4) })
            add(JLabel("Base URL:"), gbc(0, 3))
            add(baseUrlField!!, gbc(1, 3, GridBagConstraints.HORIZONTAL, 1.0))
            add(JLabel("Default model:"), gbc(0, 4))
            add(defaultModelField!!, gbc(1, 4, GridBagConstraints.HORIZONTAL, 1.0))
            add(JLabel("API key:"), gbc(0, 5))
            add(apiKeyField!!, gbc(1, 5, GridBagConstraints.HORIZONTAL, 1.0))
        }
    }

    override fun isModified(): Boolean {
        val s = PluginSettings.getInstance()
        val enteredType = if (claudeRadio?.isSelected == true) ProviderType.CLAUDE else ProviderType.OPENAI_COMPATIBLE
        return enteredType != s.providerType
            || (baseUrlField?.text ?: "") != s.baseUrl
            || (defaultModelField?.text ?: "") != s.defaultModel
            || String(apiKeyField?.password ?: charArrayOf()) != s.apiKey
    }

    override fun apply() {
        val s = PluginSettings.getInstance()
        s.providerType = if (claudeRadio?.isSelected == true) ProviderType.CLAUDE else ProviderType.OPENAI_COMPATIBLE
        s.baseUrl = baseUrlField?.text ?: s.baseUrl
        s.defaultModel = defaultModelField?.text ?: s.defaultModel
        s.apiKey = String(apiKeyField?.password ?: charArrayOf())
    }

    override fun reset() {
        val s = PluginSettings.getInstance()
        when (s.providerType) {
            ProviderType.CLAUDE -> claudeRadio?.isSelected = true
            ProviderType.OPENAI_COMPATIBLE -> openAiRadio?.isSelected = true
        }
        baseUrlField?.text = s.baseUrl
        baseUrlField?.isVisible = s.providerType == ProviderType.OPENAI_COMPATIBLE
        defaultModelField?.text = s.defaultModel
        apiKeyField?.text = s.apiKey
    }
}
