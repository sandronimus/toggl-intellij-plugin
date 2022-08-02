package com.sandronimus.intellij.plugin.toggl.services

import com.intellij.dvcs.ui.LightActionGroup
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.wm.StatusBar
import com.sandronimus.intellij.plugin.toggl.TogglBundle
import com.sandronimus.intellij.plugin.toggl.com.sandronimus.intellij.plugin.toggl.ui.TogglTimeEntryActionGroup
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry
import com.sandronimus.intellij.plugin.toggl.notifiers.TogglTimerStateNotifier
import com.sandronimus.intellij.plugin.toggl.ui.TogglActionGroupPopup
import java.text.DateFormat

class TogglPopupBuilder {
    private val togglTimerState = service<TogglTimerState>()
    private val messageBus = ApplicationManager.getApplication().messageBus
    private var actionGroup: LightActionGroup? = null

    init {
        messageBus.connect().subscribe(
            TogglTimerStateNotifier.CHANGE_LAST_TIME_ENTRIES,
            object : TogglTimerStateNotifier.ChangeLastTimeEntriesState {
                override fun lastTimeEntriesChanged(newLastTimeEntries: Array<TogglTimeEntry>) {
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
        actionGroup.addAction(object : AnAction(TogglBundle.message("action.newTimeEntry")) {
            override fun actionPerformed(e: AnActionEvent) {

            }
        })

        if (togglTimerState.activeTimeEntry !== null) {
            actionGroup.addAction(object : AnAction(TogglBundle.message("action.stop")) {
                override fun actionPerformed(e: AnActionEvent) {

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
