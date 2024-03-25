package io.openfeedback.viewmodels.extensions

import com.vanniktech.locale.Locale
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

actual fun LocalDateTime.format(pattern: String, locale: Locale): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = pattern
    dateFormatter.locale = NSLocale(locale.toString())
    return dateFormatter.stringFromDate(
        date = toNSDateComponents().date()
            ?: throw IllegalStateException("Could not convert kotlin date to NSDate $this")
    )
}
