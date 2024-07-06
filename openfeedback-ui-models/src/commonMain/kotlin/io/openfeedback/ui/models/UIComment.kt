package io.openfeedback.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class UIComment(
    val id: String,
    val message: String,
    val createdAt: String,
    val upVotes: Int,
    val dots: List<UIDot>,
    val votedByUser: Boolean,
    val fromUser: Boolean
)
