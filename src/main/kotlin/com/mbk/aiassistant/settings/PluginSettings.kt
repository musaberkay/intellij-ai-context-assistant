package com.mbk.aiassistant.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service
@State(
    name = "AIContextAssistantSettings",
    storages = [Storage("ai-context-assistant.xml")]
)
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    data class State(var apiKey: String = "")

    private var state = State()

    var apiKey: String
        get() = state.apiKey
        set(value) { state.apiKey = value }

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): PluginSettings = service()
    }
}
