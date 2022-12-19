package io.openfeedback.android.viewmodels.models

import androidx.compose.runtime.Immutable
import java.util.Date

@Immutable
data class UIComment(
    val message: String,
    val createdAt: Date
)
