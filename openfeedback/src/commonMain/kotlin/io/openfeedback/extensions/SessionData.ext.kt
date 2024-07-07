package io.openfeedback.extensions

import io.openfeedback.model.Comment
import io.openfeedback.model.SessionData
import kotlinx.datetime.Clock

/**
 * Update the [SessionData] with the new up vote on the comment.
 *
 * @param commentId Identifier of the comment.
 * @param voted true if it is an up vote, otherwise false.
 * @return copy of the [SessionData] with the up vote update.
 */
internal fun SessionData.voteComment(commentId: String, voted: Boolean): SessionData {
    val newVotedCommentIds = if (voted) {
        votedCommentIds + commentId
    } else {
        votedCommentIds - commentId
    }

    val newComments = comments.map {
        if (it.id == commentId) {
            if (voted) {
                it.copy(plus = it.plus + 1)
            } else {
                it.copy(plus = it.plus - 1)
            }
        } else {
            it
        }
    }
    return copy(
        votedCommentIds = newVotedCommentIds,
        comments = newComments
    )
}

/**
 * Update the [SessionData] with the new comment.
 *
 * @param text Content of the comment.
 * @return copy of the [SessionData] with the new comment.
 */
internal fun SessionData.commitComment(text: String): SessionData {
    var found = false
    var newComments = comments.map {
        if (it.userId == userId) {
            found = true
            it.copy(updatedAt = Clock.System.now(), text = text)
        } else {
            it
        }
    }
    if (!found) {
        newComments = newComments + Comment(
            id = "placeholderId",
            userId = userId,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            text = text,
            plus = 0
        )
    }
    return copy(
        comments = newComments.sortedByDescending { it.updatedAt }
    )
}

/**
 * Update the [SessionData] with the new vote on a vote item.
 *
 * @param voteItemId Identifier of the vote item.
 * @param voted true if it is an up vote, otherwise false.
 * @return copy of the [SessionData] with the vote on a vote item.
 */
internal fun SessionData.voteItem(voteItemId: String, voted: Boolean): SessionData {
    val newVotedItemsIds = if (voted) {
        votedItemIds + voteItemId
    } else {
        votedItemIds - voteItemId
    }

    val newAggregates = voteItemAggregates.mapValues {
        if (it.key == voteItemId) {
            if (voted) {
                it.value + 1
            } else {
                it.value - 1
            }
        } else {
            it.value
        }
    }
    return copy(
        votedItemIds = newVotedItemsIds,
        voteItemAggregates = newAggregates
    )
}
