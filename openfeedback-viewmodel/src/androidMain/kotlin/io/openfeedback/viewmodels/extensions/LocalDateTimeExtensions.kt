package io.openfeedback.viewmodels.extensions

import com.vanniktech.locale.Locale
import com.vanniktech.locale.toJavaLocale
import kotlinx.datetime.LocalDateTime
import java.text.SimpleDateFormat
import java.util.Date

actual fun LocalDateTime.format(pattern: String, locale: Locale): String {
    val formatter = SimpleDateFormat("dd MMM, hh:mm", locale.toJavaLocale())
    return formatter.format(
        Date(
            date.year,
            date.monthNumber,
            date.dayOfMonth,
            time.hour,
            time.minute,
            time.second
        )
    )
}