package com.sandronimus.intellij.plugin.toggl.notifiers

import com.intellij.util.messages.Topic
import com.intellij.util.messages.Topic.AppLevel
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry
import com.sandronimus.intellij.plugin.toggl.services.TogglTimerState

interface TogglTimerStateNotifier {
    companion object {
        @JvmField
        @AppLevel
        val CHANGE_STATE = Topic(ChangeState::class.java)

        @JvmField
        @AppLevel
        val CHANGE_LAST_TIME_ENTRIES = Topic(ChangeLastTimeEntriesState::class.java)
    }

    interface ChangeState {
        fun stateChanged(newState: TogglTimerState.State)
    }

    interface ChangeLastTimeEntriesState {
        fun lastTimeEntriesChanged(newLastTimeEntries: Array<TogglTimeEntry>)
    }
}
