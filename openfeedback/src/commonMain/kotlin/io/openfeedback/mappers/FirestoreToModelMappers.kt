package io.openfeedback.mappers

import io.openfeedback.extensions.coerceAggregations
import io.openfeedback.extensions.commentVoteItemId
import io.openfeedback.model.CommentsMap
import io.openfeedback.model.Project
import io.openfeedback.model.SessionData
import io.openfeedback.model.SessionThing
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteItemCount
import kotlinx.datetime.Instant

expect fun timestampToInstant(nativeTimestamp: Any): Instant

/**
 * Turns the openfeedback model into something that is a bit more palatable
 */
internal fun mapToSessionData(
    userId: String,
    project: Project,
    userVotes: List<UserVote>,
    sessionThings: Map<String, SessionThing>,
): SessionData {
    val votedItemIds = userVotes.mapNotNull {
        if (it.text != null) {
            // This is a comment
            return@mapNotNull null
        }
        if (it.voteItemId == project.commentVoteItemId()) {
            // In theory one cannot vote on the "text" vote item but 🤷
            return@mapNotNull null
        }
        it.voteItemId
    }.toSet()
    val votedCommentIds = userVotes.mapNotNull {
        if (it.text != null) {
            // This is a comment
            return@mapNotNull null
        }
        /**
         * Do we need to check voteItemId?
         */
//        if (it.voteItemId != project.commentVoteItemId()) {
//            return@mapNotNull null
//        }
        // If it.id is not null, it's the upvote for a comment
        it.id
    }.toSet()

    val voteItemAggregates = project.voteItems.mapNotNull { voteItem ->
        if (voteItem.type == "text") {
            return@mapNotNull null
        }

        val existing = sessionThings.get(voteItem.id)
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
    val commentMap =
        (commentsMaps.firstOrNull() ?: CommentsMap(emptyMap())).coerceAggregations(votedCommentIds)

    val comments = commentMap.all.values.sortedByDescending { it.updatedAt }
    return SessionData(
        project = project,
        userId = userId,
        votedItemIds = votedItemIds,
        votedCommentIds = votedCommentIds,
        voteItemAggregates = voteItemAggregates,
        comments = comments
    )
}
