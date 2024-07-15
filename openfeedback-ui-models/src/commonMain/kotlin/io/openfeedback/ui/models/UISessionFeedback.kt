package io.openfeedback.ui.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class UISessionFeedback(
    val comments: ImmutableList<UIComment>,
    val voteItems: ImmutableList<UIVoteItem>,
    val colors: ImmutableList<String>,
)
