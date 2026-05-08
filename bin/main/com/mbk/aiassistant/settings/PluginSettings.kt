package com.mbk.aiassistant.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

enum class ProviderType { CLAUDE, OPENAI_COMPATIBLE }

@Service
@State(
    name = "AIContextAssistantSettings",
    storages = [Storage("ai-context-assistant.xml")]
)
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    data class State(
        var apiKey: String = "",
        var providerType: ProviderType = ProviderType.OPENAI_COMPATIBLE,
        var baseUrl: String = "http://localhost:11434",
        var defaultModel: String = "llama3.2"
    )

    private var state = State()

    var apiKey: String
        get() = state.apiKey
        set(value) { state.apiKey = value }

    var providerType: ProviderType
        get() = state.providerType
        set(value) { state.providerType = value }

    var baseUrl: String
        get() = state.baseUrl
        set(value) { state.baseUrl = value }

    var defaultModel: String
        get() = state.defaultModel
        set(value) { state.defaultModel = value }

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): PluginSettings = service()
    }
}
