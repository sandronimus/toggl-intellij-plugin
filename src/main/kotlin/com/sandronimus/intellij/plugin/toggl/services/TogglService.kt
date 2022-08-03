package com.sandronimus.intellij.plugin.toggl.services

import com.google.gson.GsonBuilder
import com.intellij.openapi.components.service
import com.sandronimus.intellij.plugin.toggl.exceptions.TogglApiException
import com.sandronimus.intellij.plugin.toggl.models.api.*
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.util.EntityUtils

class TogglService {
    private val apiTokenService = service<TogglApiTokenService>()
    private val client: HttpClient
    private val gson = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()

    init {
        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(30000)
            .setSocketTimeout(30000)
            .build()

        client = HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build()
    }

    fun getUserInfo(): TogglUserInfo {
        return performTogglRequest(HttpGet("https://api.track.toggl.com/api/v9/me"))
    }

    fun getLastTimeEntries(): Array<TogglTimeEntry> {
        return performTogglRequest(HttpGet("https://api.track.toggl.com/api/v9/me/time_entries"))
    }

    fun getCurrentTimeEntry(): TogglTimeEntry? {
        return performTogglRequest(HttpGet("https://api.track.toggl.com/api/v9/me/time_entries/current"))
    }

    fun getProjects(): Array<TogglProject> {
        return performTogglRequest(HttpGet("https://api.track.toggl.com/api/v9/me/projects"))
    }

    fun startNewTimeEntry(timeEntry: TogglNewTimeEntry): TogglTimeEntry {
        val request = HttpPost("https://api.track.toggl.com/api/v9/workspaces/${timeEntry.wid}/time_entries")
        request.entity = StringEntity(gson.toJson(timeEntry), ContentType.APPLICATION_JSON)

        return performTogglRequest(request)
    }

    fun stopTimeEntry(timeEntry: TogglTimeEntryStop): TogglTimeEntry {
        val request =
            HttpPut("https://api.track.toggl.com/api/v9/workspaces/${timeEntry.workspaceId}/time_entries/${timeEntry.id}")
        request.entity = StringEntity(gson.toJson(timeEntry), ContentType.APPLICATION_JSON)

        return performTogglRequest(request)
    }

    private inline fun <reified T> performTogglRequest(request: HttpRequestBase): T {
        val context = BasicHttpContext()
        val basicAuth = BasicScheme()
        val authHeader = basicAuth.authenticate(
            UsernamePasswordCredentials(apiTokenService.getApiToken(), "api_token"),
            request,
            context
        )
        request.addHeader(authHeader)

        val response = client.execute(request, context)
        try {
            if (response.statusLine.statusCode != 200) {
                throw TogglApiException(response.entity.content.reader().readText(), response.statusLine.statusCode)
            }

            return gson.fromJson(response.entity.content.reader(), T::class.java)
        } finally {
            EntityUtils.consume(response.entity)
        }
    }
}
