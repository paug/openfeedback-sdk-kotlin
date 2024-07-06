package io.openfeedback.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class UISessionFeedback(
    val comments: List<UIComment>,
    val voteItems: List<UIVoteItem>,
    val colors: List<String>,
)
