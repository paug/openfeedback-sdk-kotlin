package io.openfeedback.viewmodels.models

import androidx.compose.runtime.Immutable
import io.openfeedback.model.Project

@Immutable
data class UISessionFeedback(
    val comments: List<UIComment>,
    val voteItems: List<UIVoteItem>,
    val colors: List<String>,
)

internal fun Project.commentVoteItemId(): String? = voteItems.find { it.type == "text" }?.id

