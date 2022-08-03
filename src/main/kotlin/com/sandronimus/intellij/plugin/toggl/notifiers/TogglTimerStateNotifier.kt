package com.sandronimus.intellij.plugin.toggl.notifiers

import com.intellij.util.messages.Topic
import com.intellij.util.messages.Topic.AppLevel
import com.sandronimus.intellij.plugin.toggl.models.api.TogglProject
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry
import com.sandronimus.intellij.plugin.toggl.services.TogglTimerState

interface TogglTimerStateNotifier {
    companion object {
        @JvmField
        @AppLevel
        val CHANGE_STATE = Topic(ChangeState::class.java)

        @JvmField
        @AppLevel
        val CHANGE_LAST_TIME_ENTRIES = Topic(ChangeLastTimeEntries::class.java)

        @JvmField
        @AppLevel
        val CHANGE_CURRENT_TIME_ENTRY = Topic(ChangeCurrentTimeEntry::class.java)

        @JvmField
        @AppLevel
        val CHANGE_PROJECTS = Topic(ChangeProjects::class.java)
    }

    interface ChangeState {
        fun stateChanged(newState: TogglTimerState.State)
    }

    interface ChangeLastTimeEntries {
        fun lastTimeEntriesChanged(newLastTimeEntries: Array<TogglTimeEntry>)
    }

    interface ChangeCurrentTimeEntry {
        fun currentTimeEntryChanged(newCurrentTimeEntry: TogglTimeEntry?)
    }

    interface ChangeProjects {
        fun projectsChanged(newProjects: HashMap<Long, TogglProject>)
    }
}
