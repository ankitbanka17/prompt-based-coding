package com.github.ankitbanka17.promptbasedcoding.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros

@State(
    name = "PluginSettingsStorage",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class PluginSettingsStorage : PersistentStateComponent<PluginSettingsStorage.State> {

    data class State(var settings: MutableMap<String, String> = mutableMapOf())

    private var state: State = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun getString(key: String): String? = state.settings[key]

    fun setString(key: String, value: String) {
        state.settings[key] = value
    }

    companion object {
        fun getInstance(): PluginSettingsStorage {
            return ServiceManager.getService(PluginSettingsStorage::class.java)
        }
    }
}
