package com.sandronimus.intellij.plugin.toggl.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TogglProject(
    @Expose
    val id: Long,

    @Expose
    val name: String,

    @SerializedName("workspace_id")
    @Expose
    val workspaceId: Long,
) {
    override fun toString(): String {
        return name
    }
}
