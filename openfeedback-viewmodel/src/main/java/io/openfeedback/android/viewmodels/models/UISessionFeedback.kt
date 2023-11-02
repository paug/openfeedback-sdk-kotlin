package io.openfeedback.android.viewmodels.models

import androidx.compose.runtime.Immutable

@Immutable
data class UISessionFeedback(
    val commentValue: String,
    val commentVoteItemId: String?,
    val comments: List<UIComment>,
    val voteItem: List<UIVoteItem>
)
