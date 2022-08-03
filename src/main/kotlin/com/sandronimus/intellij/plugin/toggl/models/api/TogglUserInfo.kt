package com.sandronimus.intellij.plugin.toggl.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TogglUserInfo(
    @SerializedName("default_workspace_id")
    @Expose
    val defaultWorkspaceId: Long,
)
