package io.openfeedback.viewmodels.extensions

import com.vanniktech.locale.Locale
import kotlinx.datetime.LocalDateTime

expect fun LocalDateTime.format(pattern: String, locale: Locale): String