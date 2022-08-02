package com.sandronimus.intellij.plugin.toggl.preloaders

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.sandronimus.intellij.plugin.toggl.services.TogglBackgroundDataUpdater

class TogglPluginLoader : PreloadingActivity() {
    private val backgroundDataUpdater = service<TogglBackgroundDataUpdater>()

    override fun preload(indicator: ProgressIndicator) {
        if (!indicator.isCanceled) {
            backgroundDataUpdater.start()
        }
    }
}
