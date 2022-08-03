package com.sandronimus.intellij.plugin.toggl.models.api

import com.google.gson.annotations.Expose
import com.sandronimus.intellij.plugin.toggl.util.DateFormatter
import java.util.*

data class TogglTimeEntryStop(
    private val timeEntry: TogglTimeEntry
) {
    @Expose
    val duration: Long

    @Expose
    val stop: String

    val id: Long
    val workspaceId: Long

    init {
        id = timeEntry.id
        workspaceId = timeEntry.workspaceId

        val currentDate = Date()
        duration = (currentDate.time - timeEntry.start.time) / 1000

        stop = DateFormatter().formatDateForApi(Date())
    }
}
