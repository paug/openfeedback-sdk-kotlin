package io.openfeedback.android.viewmodels.models

import androidx.compose.runtime.Immutable
import java.util.Date

@Immutable
data class UIComment(
    val id: String,
    val message: String,
    val createdAt: String,
    val upVotes: Int,
    val dots: List<UIDot>
)
