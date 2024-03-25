package io.openfeedback.viewmodels.models

import androidx.compose.runtime.Immutable

@Immutable
data class UIVoteItem(
    val id: String,
    val text: String,
    val dots: List<UIDot>,
    val votedByUser: Boolean
)
