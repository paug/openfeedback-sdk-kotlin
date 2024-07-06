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

internal sealed interface Event
internal class CommitComment(
    val text: String
) : Event

internal class VoteItemEvent(
    val voteItemId: String,
    val votedByUser: Boolean
) : Event

internal class VoteCommentEvent(
    val commentId: String,
    val votedByUser: Boolean
) : Event
