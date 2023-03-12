package io.openfeedback.models

import kotlinx.serialization.Serializable

@Serializable
class Project(
    val chipColors: List<String> = emptyList(),
    val voteItems: List<VoteItem> = emptyList()
)
