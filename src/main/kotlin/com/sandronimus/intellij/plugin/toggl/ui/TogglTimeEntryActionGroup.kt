package com.sandronimus.intellij.plugin.toggl.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.sandronimus.intellij.plugin.toggl.TogglBundle
import com.sandronimus.intellij.plugin.toggl.models.api.TogglNewTimeEntry
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry
import com.sandronimus.intellij.plugin.toggl.services.TogglBackgroundDataUpdater
import com.sandronimus.intellij.plugin.toggl.services.TogglNotifier
import com.sandronimus.intellij.plugin.toggl.services.TogglService
import com.sandronimus.intellij.plugin.toggl.services.TogglTimerState

class TogglTimeEntryActionGroup(val timeEntry: TogglTimeEntry) : ActionGroup("", true) {
    private val togglTimerState = service<TogglTimerState>()
    private val backgroundDataUpdater = service<TogglBackgroundDataUpdater>()
    private val togglApi = service<TogglService>()
    private val togglNotifier = service<TogglNotifier>()

    init {
        var text = timeEntry.description
        if (togglTimerState.projects.containsKey(timeEntry.projectId)) {
            text += " - ${togglTimerState.projects[timeEntry.projectId]?.name}"
        }
        templatePresentation.text = text
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            object : AnAction(TogglBundle.message("action.startAgain")) {
                override fun actionPerformed(e: AnActionEvent) {
                    ApplicationManager.getApplication().executeOnPooledThread {
                        try {
                            togglApi.startNewTimeEntry(
                                TogglNewTimeEntry(
                                    timeEntry.description,
                                    timeEntry.projectId,
                                    timeEntry.workspaceId
                                )
                            )
                        } catch (e: Throwable) {
                            togglNotifier.notifyAboutTogglException(e)
                        }

                        backgroundDataUpdater.updateDataAfterTimeEntryActions()
                    }
                }
            },
        )
    }
}
