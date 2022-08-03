package com.sandronimus.intellij.plugin.toggl.exceptions

class TogglApiException(message: String, val statusCode: Int) : Exception(message) {
}
