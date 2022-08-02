package com.sandronimus.intellij.plugin.toggl.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class TogglStatusBarWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String {
        return this.javaClass.name
    }

    override fun getDisplayName(): String {
        return "Toggl Timer"
    }

    override fun isAvailable(project: Project): Boolean {
        return true
    }

    override fun createWidget(project: Project): StatusBarWidget {
        return TogglStatusBarWidget()
    }

    override fun disposeWidget(widget: StatusBarWidget) {
        Disposer.dispose(widget)
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        return true
    }
}
