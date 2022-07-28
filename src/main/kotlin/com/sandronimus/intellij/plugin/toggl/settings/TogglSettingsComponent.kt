package com.sandronimus.intellij.plugin.toggl.settings

import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import java.awt.KeyboardFocusManager
import javax.swing.JPanel

class TogglSettingsComponent(private var _passwordNotEmpty: Boolean) {
    private var settingsPanel: JPanel
    private var apiTokenField: JBPasswordField = JBPasswordField()
    var passwordNotEmpty: Boolean
        get() = _passwordNotEmpty
        set(value) {
            _passwordNotEmpty = value
        }

    init {
        @Suppress("DialogTitleCapitalization")
        settingsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("API Token", apiTokenField)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        if (passwordNotEmpty) {
            apiTokenField.setPasswordIsStored(true)
        }
    }

    fun getPanel(): JPanel {
        return settingsPanel
    }

    fun getApiToken(): CharArray? {
        return apiTokenField.password
    }

    fun resetPasswordFieldState() {
        if (apiTokenField.hasFocus()) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner()
        }

        apiTokenField.text = ""
        apiTokenField.setPasswordIsStored(passwordNotEmpty)
    }
}

