package com.sandronimus.intellij.plugin.toggl.services

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

class TogglApiTokenService {
    private val credentialAttributes = CredentialAttributes(generateServiceName("Toggl Plugin", "API Key"))
    private var apiToken = PasswordSafe.instance.getPassword(credentialAttributes)

    fun getApiToken(): String? {
        return apiToken
    }

    fun saveApiToken(apiToken: String?) {
        this.apiToken = apiToken
        PasswordSafe.instance.setPassword(credentialAttributes, apiToken)
    }
}
