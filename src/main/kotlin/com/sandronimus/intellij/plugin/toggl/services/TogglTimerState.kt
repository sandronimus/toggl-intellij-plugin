package com.sandronimus.intellij.plugin.toggl.services

import com.intellij.openapi.application.ApplicationManager
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry
import com.sandronimus.intellij.plugin.toggl.notifiers.TogglTimerStateNotifier

class TogglTimerState {
    private val stateNotifier =
        ApplicationManager.getApplication().messageBus.syncPublisher(TogglTimerStateNotifier.CHANGE_STATE)
    private val lastTimeEntriesNotifier =
        ApplicationManager.getApplication().messageBus.syncPublisher(TogglTimerStateNotifier.CHANGE_LAST_TIME_ENTRIES)

    enum class State {
        Unknown,
        Idle,
        Tracking,
    }

    private var _state = State.Unknown
    var state: State
        get() = _state
        set(value) {
            _state = value

            stateNotifier.stateChanged(value)
        }

    private var _activeTimeEntry: TogglTimeEntry? = null
    var activeTimeEntry: TogglTimeEntry?
        get() = _activeTimeEntry
        set(value) {
            _activeTimeEntry = value

            state = when (value) {
                null -> State.Idle
                else -> State.Tracking
            }
        }

    private var _lastTimeEntries: Array<TogglTimeEntry> = emptyArray()
    var lastTimeEntries: Array<TogglTimeEntry>
        get() {
            return _lastTimeEntries
        }
        set(value) {
            _lastTimeEntries = value

            lastTimeEntriesNotifier.lastTimeEntriesChanged(value)
        }
}
