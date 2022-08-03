package com.sandronimus.intellij.plugin.toggl.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.sandronimus.intellij.plugin.toggl.services.TogglApiTokenService
import com.sandronimus.intellij.plugin.toggl.services.TogglBackgroundDataUpdater
import javax.swing.JComponent

class TogglSettings: Configurable {
    private var togglApiTokenService: TogglApiTokenService = service()
    private val backgroundDataUpdater = service<TogglBackgroundDataUpdater>()
    private lateinit var component: TogglSettingsComponent

    override fun createComponent(): JComponent {
        var passwordNotEmpty = false
        if (togglApiTokenService.getApiToken()?.isNotEmpty() == true) {
            passwordNotEmpty = true
        }

        component = TogglSettingsComponent(passwordNotEmpty)
        return component.getPanel()
    }

    override fun isModified(): Boolean {
        val apiToken = component.getApiToken()?.let { String(it) } ?: return false

        if (apiToken.isEmpty()) {
            return false
        }
        return apiToken != togglApiTokenService.getApiToken()
    }

    override fun apply() {
        val apiToken = String(component.getApiToken()!!)
        togglApiTokenService.saveApiToken(apiToken)
        component.passwordNotEmpty = true
        component.resetPasswordFieldState()

        backgroundDataUpdater.updateAll()
    }

    override fun reset() {
        component.resetPasswordFieldState()
    }

    override fun getDisplayName(): String {
        return "Toggl"
    }
}
