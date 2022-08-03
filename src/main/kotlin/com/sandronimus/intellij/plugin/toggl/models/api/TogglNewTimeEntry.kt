package com.sandronimus.intellij.plugin.toggl.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sandronimus.intellij.plugin.toggl.util.DateFormatter
import java.util.*

data class TogglNewTimeEntry(
    @Expose
    val description: String,

    private val projectId: Long?,
    private val workspaceId: Long,
) {
    @SerializedName("created_with")
    @Expose
    val createdWith: String = "Toggl IntelliJ Plugin"

    @Expose
    val duration = -Date().time / 1000

    @Expose
    val pid = projectId

    @Expose
    val start: String = DateFormatter().formatDateForApi(Date())

    @Expose
    val stop = null

    @Expose
    val tags = null

    @Expose
    val tid = null

    @Expose
    val wid = workspaceId
}
