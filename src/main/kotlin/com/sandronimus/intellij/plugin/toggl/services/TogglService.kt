package com.sandronimus.intellij.plugin.toggl.services

import com.google.gson.Gson
import com.intellij.openapi.components.service
import com.sandronimus.intellij.plugin.toggl.models.api.TogglTimeEntry
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.BasicHttpContext

class TogglService {
    private val apiTokenService = service<TogglApiTokenService>()
    private val client = HttpClients.createDefault()
    private val gson = Gson()

    fun getLastTimeEntries(): Array<TogglTimeEntry> {
        return performTogglRequest(HttpGet("https://api.track.toggl.com/api/v9/me/time_entries"))
    }

    fun getCurrentTimeEntry(): TogglTimeEntry? {
        return performTogglRequest(HttpGet("https://api.track.toggl.com/api/v9/me/time_entries/current"))
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

        return gson.fromJson(response.entity.content.reader(), T::class.java)
    }
}
