package com.github.ankitbanka17.promptbasedcoding.services

import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.*
import java.awt.Component

class PluginConfigurable : SearchableConfigurable {

    private var apiKeyField: JTextField? = null
    private var customPathField: JTextField? = null
    private var someSettingField: JTextField? = null

    override fun getId(): String = "ankit.configuration"

    override fun getDisplayName(): String = "Ankit Configuration"

    override fun isModified(): Boolean {
        val settings = PluginSettingsStorage.getInstance()
        return apiKeyField?.text != settings.getString("openai.api.key") ||
                customPathField?.text != settings.getString("plugin.custom.path") ||
                someSettingField?.text != settings.getString("plugin.some.setting")
    }

    override fun apply() {
        val settings = PluginSettingsStorage.getInstance()
        settings.setString("openai.api.key", apiKeyField?.text.orEmpty())
        settings.setString("plugin.custom.path", customPathField?.text.orEmpty())
        settings.setString("plugin.some.setting", someSettingField?.text.orEmpty())
    }

    override fun reset() {
        val settings = PluginSettingsStorage.getInstance()
        apiKeyField?.text = settings.getString("openai.api.key") ?: ""
        customPathField?.text = settings.getString("plugin.custom.path") ?: ""
        someSettingField?.text = settings.getString("plugin.some.setting") ?: ""
    }

    override fun createComponent(): JComponent {
        println("Loading PluginConfigurable UI...")

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        // OpenAI API Key input field
        apiKeyField = JTextField().apply {
            alignmentX = Component.LEFT_ALIGNMENT
            text = PluginSettingsStorage.getInstance().getString("openai.api.key") ?: ""
        }
        panel.add(JLabel("OpenAI API Key:"))
        panel.add(apiKeyField)

        // Custom Path input field
        customPathField = JTextField().apply {
            alignmentX = Component.LEFT_ALIGNMENT
            text = PluginSettingsStorage.getInstance().getString("plugin.custom.path") ?: ""
        }
        panel.add(JLabel("Custom Path:"))
        panel.add(customPathField)

        // Some other setting input field
        someSettingField = JTextField().apply {
            alignmentX = Component.LEFT_ALIGNMENT
            text = PluginSettingsStorage.getInstance().getString("plugin.some.setting") ?: ""
        }
        panel.add(JLabel("Some Setting:"))
        panel.add(someSettingField)

        return panel
    }
}
