package io.openfeedback.ui.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class UIComment(
    val id: String,
    val message: String,
    val createdAt: String,
    val upVotes: Int,
    val dots: ImmutableList<UIDot>,
    val votedByUser: Boolean,
    val fromUser: Boolean
)
