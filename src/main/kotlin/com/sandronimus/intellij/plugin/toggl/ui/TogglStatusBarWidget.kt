package com.sandronimus.intellij.plugin.toggl.ui

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import com.intellij.util.concurrency.annotations.RequiresEdt
import com.sandronimus.intellij.plugin.toggl.models.api.TogglProject
import com.sandronimus.intellij.plugin.toggl.notifiers.TogglTimerStateNotifier
import com.sandronimus.intellij.plugin.toggl.services.TogglPopupBuilder
import com.sandronimus.intellij.plugin.toggl.services.TogglTimerState
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit

class TogglStatusBarWidget : StatusBarWidget, StatusBarWidget.MultipleTextValuesPresentation {
    private val togglTimerState = service<TogglTimerState>()
    private val togglPopupBuilder = service<TogglPopupBuilder>()
    private val messageBus = ApplicationManager.getApplication().messageBus

    private var statusBar: StatusBar? = null
    private var text = "Unknown"

    override fun dispose() {
        Disposer.dispose(this)

        statusBar = null
    }

    override fun ID(): String {
        return this.javaClass.name
    }

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar

        messageBus.connect()
            .subscribe(TogglTimerStateNotifier.CHANGE_STATE, object : TogglTimerStateNotifier.ChangeState {
                override fun stateChanged(newState: TogglTimerState.State) {
                    updateLater()
                }
            })

        messageBus.connect()
            .subscribe(TogglTimerStateNotifier.CHANGE_PROJECTS, object : TogglTimerStateNotifier.ChangeProjects {
                override fun projectsChanged(newProjects: HashMap<Long, TogglProject>) {
                    updateLater()
                }
            })

        val future =
            JobScheduler.getScheduler().scheduleWithFixedDelay({ updateLater() }, 500, 500, TimeUnit.MILLISECONDS)
        Disposer.register(this) { future.cancel(false) }

        Disposer.register(statusBar, this)
        updateLater()
    }

    override fun getTooltipText(): String {
        return ""
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getPopupStep(): ListPopup {
        return togglPopupBuilder.getPopup(statusBar)
    }

    override fun getSelectedValue(): String {
        return "Toggl: $text"
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }

    private fun updateLater() {
        ApplicationManager.getApplication().invokeLater { update() }
    }

    @RequiresEdt
    private fun update() {
        text = when (togglTimerState.state) {
            TogglTimerState.State.Unknown -> "Unknown"
            TogglTimerState.State.Idle -> "Idle"
            TogglTimerState.State.Tracking -> togglTimerState.activeTimeEntry?.fullDescription ?: "No description"
        }

        statusBar?.updateWidget(ID())
    }
}
