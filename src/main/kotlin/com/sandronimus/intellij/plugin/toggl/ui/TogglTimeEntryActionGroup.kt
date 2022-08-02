package com.sandronimus.intellij.plugin.toggl.com.sandronimus.intellij.plugin.toggl.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.sandronimus.intellij.plugin.toggl.TogglBundle
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry

class TogglTimeEntryActionGroup(timeEntry: TogglTimeEntry) : ActionGroup("", true) {
    init {
        templatePresentation.text = timeEntry.description
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            object : AnAction(TogglBundle.message("action.startAgain")) {
                override fun actionPerformed(e: AnActionEvent) {
                    TODO("Not yet implemented")
                }
            },
        )
    }
}
