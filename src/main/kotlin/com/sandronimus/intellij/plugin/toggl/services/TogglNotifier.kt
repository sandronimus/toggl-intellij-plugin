package com.sandronimus.intellij.plugin.toggl.services

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.sandronimus.intellij.plugin.toggl.TogglBundle
import com.sandronimus.intellij.plugin.toggl.exceptions.TogglApiException

class TogglNotifier {
    fun notifyAboutTogglException(e: Throwable) {
        if (e is TogglApiException && e.statusCode == 403) {
            notifyAboutInvalidToken()
        } else {
            notifyAboutUnknownError()
        }
    }

    private fun notifyAboutInvalidToken() {
        ApplicationManager.getApplication().invokeLater {
            NotificationGroupManager.getInstance().getNotificationGroup("Toggl")
                .createNotification(NotificationType.ERROR)
                .setTitle("Toggl")
                .setContent(TogglBundle.message("notification.invalidToken"))
                .notify(null)
        }
    }

    private fun notifyAboutUnknownError() {
        ApplicationManager.getApplication().invokeLater {
            NotificationGroupManager.getInstance().getNotificationGroup("Toggl")
                .createNotification(NotificationType.ERROR)
                .setTitle("Toggl")
                .setContent(TogglBundle.message("notification.unknownError"))
                .notify(null)
        }
    }
}
