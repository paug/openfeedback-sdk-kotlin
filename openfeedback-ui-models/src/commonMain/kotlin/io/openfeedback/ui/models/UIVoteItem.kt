package io.openfeedback.ui.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class UIVoteItem(
    val id: String,
    val text: String,
    val dots: ImmutableList<UIDot>,
    val votedByUser: Boolean
)
