package com.sandronimus.intellij.plugin.toggl.services

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Disposer
import com.sandronimus.intellij.plugin.toggl.models.api.TogglProject
import java.util.concurrent.TimeUnit

class TogglBackgroundDataUpdater : Disposable {
    private val togglApi = service<TogglService>()
    private val togglTimerState = service<TogglTimerState>()
    private val togglNotifier = service<TogglNotifier>()
    private val logger = logger<TogglBackgroundDataUpdater>()

    fun start() {
        val currentTimeEntryFuture =
            JobScheduler.getScheduler().scheduleWithFixedDelay({ updateCurrentTimeEntry() }, 5, 60, TimeUnit.SECONDS)

        val initialDataFuture = JobScheduler.getScheduler().schedule({
            updateUserInfo()
            updateLastTimeEntries()
            updateProjects()
        }, 2, TimeUnit.SECONDS)

        Disposer.register(this) {
            initialDataFuture.cancel(false)
            currentTimeEntryFuture.cancel(false)
        }
    }

    fun updateDataAfterTimeEntryActions() {
        updateCurrentTimeEntry()
        updateLastTimeEntries()
    }

    fun updateAll() {
        ApplicationManager.getApplication().executeOnPooledThread {
            updateUserInfo()
            updateLastTimeEntries()
            updateProjects()
            updateCurrentTimeEntry()
        }
    }

    private fun updateUserInfo() {
        try {
            togglTimerState.userInfo = togglApi.getUserInfo()
        } catch (e: Throwable) {
            togglTimerState.state = TogglTimerState.State.Unknown
            logger.warn("Unable to update user info", e)

            togglNotifier.notifyAboutTogglException(e)
        }
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

    private fun updateProjects() {
        try {
            val projects = HashMap<Long, TogglProject>()
            togglApi.getProjects().forEach {
                projects[it.id] = it
            }
            togglTimerState.projects = projects
        } catch (e: Throwable) {
            togglTimerState.state = TogglTimerState.State.Unknown
            logger.warn("Unable to update last time entries", e)
        }
    }

    override fun dispose() {

    }
}
