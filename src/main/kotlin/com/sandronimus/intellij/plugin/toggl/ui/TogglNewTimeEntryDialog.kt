package com.sandronimus.intellij.plugin.toggl.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import com.sandronimus.intellij.plugin.toggl.TogglBundle
import com.sandronimus.intellij.plugin.toggl.models.api.TogglProject
import com.sandronimus.intellij.plugin.toggl.services.TogglTimerState
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel

class TogglNewTimeEntryDialog : DialogWrapper(null) {
    private val togglTimerState = service<TogglTimerState>()
    private var description = ""
    private var projectsComboBoxModel: ComboBoxModel<TogglProject>
    private var project: TogglProject? = null

    init {
        title = TogglBundle.message("dialog.title.newTimeEntry")
        projectsComboBoxModel = DefaultComboBoxModel(
            arrayOf(
                TogglProject(0, "No Project", 0),
            ) + togglTimerState.projects.values
        )

        init()
    }

    override fun createCenterPanel() = panel {
        row(TogglBundle.message("dialog.label.description")) {
            textField(::description, { description = it }).focused()
        }
        row(TogglBundle.message("dialog.label.project")) {
            comboBox(projectsComboBoxModel, { project }, { project = it })
        }
    }

    fun getResult(): Result {
        return Result(description, project)
    }

    data class Result(
        val description: String,
        val project: TogglProject?,
    )
}
