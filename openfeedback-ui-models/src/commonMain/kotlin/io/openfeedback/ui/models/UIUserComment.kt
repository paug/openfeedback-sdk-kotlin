package io.openfeedback.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class UIUserComment(
    val id: String,
    val message: String,
    val isHidden: Boolean = false,
)
