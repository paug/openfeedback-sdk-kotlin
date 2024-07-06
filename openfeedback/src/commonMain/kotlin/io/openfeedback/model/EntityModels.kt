package io.openfeedback.model

data class SessionData(
    val project: Project,
    val userId: String,
    val votedItemIds: Set<String>,
    val votedCommentIds: Set<String>,
    /**
     * The aggregate counter for voteItems.
     * The counter for comments is in [comments]
     *
     * key is a voteItemId
     */
    val voteItemAggregates: Map<String, Long>,
    val comments: List<Comment>,
)
