package io.openfeedback.viewmodels

import android.content.Context

actual typealias PlatformContext = AndroidContext
data class AndroidContext(val context: Context)
