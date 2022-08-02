package com.sandronimus.intellij.plugin.toggl.services

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Disposer
import java.util.concurrent.TimeUnit

class TogglBackgroundDataUpdater : Disposable {
    private val togglApi = service<TogglService>()
    private val togglTimerState = service<TogglTimerState>()
    private val logger = logger<TogglBackgroundDataUpdater>()

    fun start() {
        val future =
            JobScheduler.getScheduler().scheduleWithFixedDelay({ updateCurrentTimeEntry() }, 5, 60, TimeUnit.SECONDS)
        Disposer.register(this) { future.cancel(false) }

        ApplicationManager.getApplication().invokeLater { updateLastTimeEntries() }
    }

    private fun updateCurrentTimeEntry() {
        try {
            togglTimerState.activeTimeEntry = togglApi.getCurrentTimeEntry()
        } catch (e: Throwable) {
            togglTimerState.state = TogglTimerState.State.Unknown
            logger.warn("Unable to update current time entry", e)
        }
    }

    private fun updateLastTimeEntries() {
        try {
            togglTimerState.lastTimeEntries = togglApi.getLastTimeEntries()
        } catch (e: Throwable) {
            togglTimerState.state = TogglTimerState.State.Unknown
            logger.warn("Unable to update last time entries", e)
        }
    }

    override fun dispose() {

    }
}
