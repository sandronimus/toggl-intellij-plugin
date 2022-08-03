package com.sandronimus.intellij.plugin.toggl.services

import com.intellij.dvcs.ui.LightActionGroup
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.wm.StatusBar
import com.sandronimus.intellij.plugin.toggl.TogglBundle
import com.sandronimus.intellij.plugin.toggl.models.api.TogglNewTimeEntry
import com.sandronimus.intellij.plugin.toggl.models.api.TogglProject
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntryStop
import com.sandronimus.intellij.plugin.toggl.notifiers.TogglTimerStateNotifier
import com.sandronimus.intellij.plugin.toggl.ui.TogglActionGroupPopup
import com.sandronimus.intellij.plugin.toggl.ui.TogglNewTimeEntryDialog
import com.sandronimus.intellij.plugin.toggl.ui.TogglTimeEntryActionGroup
import java.text.DateFormat

class TogglPopupBuilder {
    private val togglTimerState = service<TogglTimerState>()
    private val togglApi = service<TogglService>()
    private val backgroundDataUpdater = service<TogglBackgroundDataUpdater>()
    private val togglNotifier = service<TogglNotifier>()
    private val messageBus = ApplicationManager.getApplication().messageBus
    private var actionGroup: LightActionGroup? = null

    init {
        messageBus.connect().subscribe(
            TogglTimerStateNotifier.CHANGE_LAST_TIME_ENTRIES,
            object : TogglTimerStateNotifier.ChangeLastTimeEntries {
                override fun lastTimeEntriesChanged(newLastTimeEntries: Array<TogglTimeEntry>) {
                    actionGroup = buildActionGroup()
                }
            })

        messageBus.connect().subscribe(
            TogglTimerStateNotifier.CHANGE_PROJECTS,
            object : TogglTimerStateNotifier.ChangeProjects {
                override fun projectsChanged(newProjects: HashMap<Long, TogglProject>) {
                    actionGroup = buildActionGroup()
                }
            })

        messageBus.connect().subscribe(
            TogglTimerStateNotifier.CHANGE_CURRENT_TIME_ENTRY,
            object : TogglTimerStateNotifier.ChangeCurrentTimeEntry {
                override fun currentTimeEntryChanged(newCurrentTimeEntry: TogglTimeEntry?) {
                    actionGroup = buildActionGroup()
                }
            })
    }

    fun getPopup(statusBar: StatusBar?): TogglActionGroupPopup {
        if (actionGroup === null) {
            actionGroup = buildActionGroup()
        }

        return TogglActionGroupPopup(
            actionGroup!!,
            DataManager.getInstance().getDataContext(statusBar?.component)
        )
    }

    private fun buildActionGroup(): LightActionGroup {
        val actionGroup = LightActionGroup(false)
        if (togglTimerState.state == TogglTimerState.State.Unknown) {
            actionGroup.addAction(object : AnAction(TogglBundle.message("action.checkToken")) {
                override fun actionPerformed(e: AnActionEvent) {

                }
            })
            return actionGroup
        }

        actionGroup.addAction(object : AnAction(TogglBundle.message("action.newTimeEntry")) {
            override fun actionPerformed(e: AnActionEvent) {
                val dialog = TogglNewTimeEntryDialog()
                if (!dialog.showAndGet()) {
                    return
                }

                val result = dialog.getResult()

                var projectId: Long? = null
                val project = result.project
                if (project is TogglProject && project.id != 0L) {
                    projectId = project.id
                }

                val userInfo = togglTimerState.userInfo
                if (userInfo == null) {
                    return
                }

                var workspaceId: Long = userInfo.defaultWorkspaceId
                if (project is TogglProject && project.workspaceId != 0L) {
                    workspaceId = project.workspaceId
                }

                ApplicationManager.getApplication().executeOnPooledThread {
                    try {
                        togglApi.startNewTimeEntry(TogglNewTimeEntry(result.description, projectId, workspaceId))
                    } catch (e: Throwable) {
                        togglNotifier.notifyAboutTogglException(e)
                    }

                    backgroundDataUpdater.updateDataAfterTimeEntryActions()
                }
            }
        })

        if (togglTimerState.activeTimeEntry !== null) {
            actionGroup.addAction(object : AnAction(TogglBundle.message("action.stop")) {
                override fun actionPerformed(e: AnActionEvent) {
                    ApplicationManager.getApplication().executeOnPooledThread {
                        try {
                            val activeTimeEntry = togglTimerState.activeTimeEntry

                            if (activeTimeEntry is TogglTimeEntry) {
                                togglApi.stopTimeEntry(
                                    TogglTimeEntryStop(activeTimeEntry)
                                )
                            }
                        } catch (e: Throwable) {
                            togglNotifier.notifyAboutTogglException(e)
                        }

                        backgroundDataUpdater.updateDataAfterTimeEntryActions()
                    }
                }
            })
        }

        val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)
        var lastDateString = ""
        togglTimerState.lastTimeEntries.forEach {
            val currentDateString = dateFormat.format(it.start)
            if (currentDateString != lastDateString) {
                actionGroup.addSeparator(currentDateString)
                lastDateString = currentDateString
            }
            actionGroup.addAction(TogglTimeEntryActionGroup(it))
        }

        return actionGroup
    }
}
