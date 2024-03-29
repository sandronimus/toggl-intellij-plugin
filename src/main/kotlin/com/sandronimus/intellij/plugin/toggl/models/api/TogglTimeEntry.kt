package com.sandronimus.intellij.plugin.toggl.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class TogglTimeEntry(
    @Expose
    val start: Date,

    @Expose
    val description: String,

    @Expose
    val id: Long,

    @SerializedName("project_id")
    @Expose
    val projectId: Long?,

    @SerializedName("workspace_id")
    @Expose
    val workspaceId: Long,
) {
    private val durationString: String
        get() {
            var duration = (Date().time - start.time) / 1000
            val days = duration / (24 * 60 * 60)
            duration -= days * 24 * 60 * 60
            val hours = duration / (60 * 60)
            duration -= hours * 60 * 60
            val minutes = duration / 60
            duration -= minutes * 60
            val seconds = duration

            var result = ""
            if (days > 0) {
                result += "$days d "
            }
            result += "%02d:".format(hours)
            result += "%02d:".format(minutes)
            result += "%02d".format(seconds)
            return result
        }

    val fullDescription: String
        get() {
            return "$description (${durationString})"
        }
}
