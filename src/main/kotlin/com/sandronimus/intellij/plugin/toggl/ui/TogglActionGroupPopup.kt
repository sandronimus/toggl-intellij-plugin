package com.sandronimus.intellij.plugin.toggl.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.ui.popup.PopupFactoryImpl.ActionGroupPopup

class TogglActionGroupPopup(actionGroup: ActionGroup, dataContext: DataContext) :
    ActionGroupPopup("Toggl", actionGroup, dataContext, false, false, false, false, null, 10, null, null) {

}
