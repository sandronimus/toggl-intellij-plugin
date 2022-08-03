package com.sandronimus.intellij.plugin.toggl.util

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {
    fun formatDateForApi(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormat.format(date)
    }
}
