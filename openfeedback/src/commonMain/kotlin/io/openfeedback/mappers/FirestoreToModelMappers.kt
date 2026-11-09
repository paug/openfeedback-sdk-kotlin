package io.openfeedback.mappers

import io.openfeedback.extensions.coerceAggregations
import io.openfeedback.extensions.commentVoteItemId
import io.openfeedback.model.CommentsMap
import io.openfeedback.model.Project
import io.openfeedback.model.SessionData
import io.openfeedback.model.SessionThing
import io.openfeedback.model.UserComment
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteItemCount
import io.openfeedback.model.VoteStatus
import kotlin.time.Instant

internal expect fun timestampToInstant(nativeTimestamp: Any): Instant

/**
 * Turns the openfeedback model into something that is a bit more palatable
 */
internal fun mapToSessionData(
    userId: String,
    project: Project,
    userVotes: List<UserVote>,
    sessionThings: Map<String, SessionThing>,
): SessionData {
    val commentVoteItemId = project.commentVoteItemId()

    val votedItemIds = userVotes.mapNotNull {
        when (it) {
            is UserVote.BooleanVote -> {
                // In theory one cannot vote on the "text" vote item but 🤷
                if (it.voteItemId == commentVoteItemId) null else it.voteItemId
            }
            else -> null
        }
    }.toSet()

    val votedCommentIds = userVotes.mapNotNull {
        when (it) {
            is UserVote.CommentUpvote -> it.commentId
            else -> null
        }
    }.toSet()

    val voteItemAggregates = project.voteItems.mapNotNull { voteItem ->
        if (voteItem.type == "text") {
            return@mapNotNull null
        }

        val existing = sessionThings[voteItem.id]
        if (existing != null && existing is VoteItemCount) {
            /**
             * Be robust to negative votes
             */
            val minValue = if (votedCommentIds.contains(voteItem.id)) {
                1L
            } else {
                0L
            }
            voteItem.id to existing.count.coerceAtLeast(minValue)
        } else {
            /**
             * No document yet, return 0
             */
            voteItem.id to 0L
        }
    }.toMap()

    val commentsMaps = sessionThings.values.filterIsInstance<CommentsMap>()
    if (commentsMaps.size > 1) {
        val keys = sessionThings.filter { it.value is CommentsMap }.keys
        println("Several comment maps for voteItemIds = '$keys'.")
    }

    // Session may have multiple comment maps, mostly due to the following actions :
    // vote option reset, vote option changes, multiple "text" vote options, etc.
    // We keep the one whose ID matches the "commentVoteItemId" or the first one as a fallback.
    val commentMap = (sessionThings[commentVoteItemId] as? CommentsMap
        ?: commentsMaps.firstOrNull()
        ?: CommentsMap(emptyMap())
    ).coerceAggregations(votedCommentIds)

    val comments = commentMap.all.values.sortedByDescending { it.updatedAt }

    // Build user comment if it exists.
    val userComment = commentVoteItemId?.let { commentVoteItemId ->
        buildUserComment( commentVoteItemId, userVotes)
    }

    return SessionData(
        project = project,
        userId = userId,
        votedItemIds = votedItemIds,
        votedCommentIds = votedCommentIds,
        voteItemAggregates = voteItemAggregates,
        comments = comments,
        userComment = userComment,
    )
}

private fun buildUserComment(
    commentVoteItemId: String,
    userVotes: List<UserVote>,
): UserComment? {
    val textVote = userVotes.filterIsInstance<UserVote.TextVote>()
        .firstOrNull { it.voteItemId == commentVoteItemId } ?: return null

    val status = when (textVote.status) {
        VoteStatus.Hidden.value -> VoteStatus.Hidden
        else -> VoteStatus.Active
    }

    return UserComment(
        id = textVote.id,
        text = textVote.text,
        status = status
    )
}
