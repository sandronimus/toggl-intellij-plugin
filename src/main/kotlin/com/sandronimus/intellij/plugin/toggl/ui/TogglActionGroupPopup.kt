package com.sandronimus.intellij.plugin.toggl.ui

import com.intellij.dvcs.ui.LightActionGroup
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.ui.ActiveComponent
import com.intellij.ui.popup.PopupFactoryImpl.ActionGroupPopup
import com.intellij.util.ui.JBUI
import com.sandronimus.intellij.plugin.toggl.services.TogglBackgroundDataUpdater
import javax.swing.JComponent

class TogglActionGroupPopup(actionGroup: ActionGroup, dataContext: DataContext) :
    ActionGroupPopup("Toggl", actionGroup, dataContext, false, false, false, false, null, 10, null, null) {
    private val backgroundDataUpdater = service<TogglBackgroundDataUpdater>()

    init {
        val toolbarActionGroup = LightActionGroup(false)
        val refreshDataAction = object : AnAction(AllIcons.Actions.Refresh) {
            override fun actionPerformed(e: AnActionEvent) {
                backgroundDataUpdater.updateAll()
            }
        }
        toolbarActionGroup.addAction(refreshDataAction)

        val toolbar = ActionManager.getInstance().createActionToolbar("Toggl", toolbarActionGroup, true)
        toolbar.component.isOpaque = false

        title.setButtonComponent(object : ActiveComponent.Adapter() {
            override fun getComponent(): JComponent {
                return toolbar.component
            }
        }, JBUI.Borders.emptyRight(2))
    }
}
