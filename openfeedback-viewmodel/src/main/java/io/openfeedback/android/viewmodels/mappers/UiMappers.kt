package io.openfeedback.android.viewmodels.mappers

import io.openfeedback.android.model.Project
import io.openfeedback.android.model.SessionVotes
import io.openfeedback.android.viewmodels.models.UIComment
import io.openfeedback.android.viewmodels.models.UIDot
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UISessionFeedbackWithColors
import io.openfeedback.android.viewmodels.models.UIVoteItem
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.random.Random

fun convertToUiSessionFeedback(
    project: Project,
    userVotes: List<String>,
    totalVotes: SessionVotes,
    locale: Locale
): UISessionFeedback {
    val formatter = SimpleDateFormat("dd MMMM yyyy, hh:mm", locale)
    return UISessionFeedback(
        comments = totalVotes.comments.map { commentItem ->
            UIComment(
                id = commentItem.value.id,
                voteItemId = commentItem.value.voteItemId,
                message = commentItem.value.text,
                createdAt = formatter.format(commentItem.value.createdAt.toDate()),
                upVotes = commentItem.value.plus.toInt(),
                dots = dots(commentItem.value.plus.toInt(), project.chipColors),
                votedByUser = userVotes.contains(commentItem.value.voteItemId)
            )
        },
        voteItem = project.voteItems
            .filter { it.type == "boolean" }
            .map { voteItem ->
                val count = totalVotes.votes[voteItem.id]?.toInt() ?: 0
                UIVoteItem(
                    id = voteItem.id,
                    text = voteItem.localizedName(locale.language),
                    dots = dots(count, project.chipColors),
                    votedByUser = userVotes.contains(voteItem.id)
                )
            }
    )
}

fun UISessionFeedbackWithColors.convertToUiSessionFeedback(
    oldSessionFeedback: UISessionFeedback?
): UISessionFeedback = UISessionFeedback(
    comments = this.session.comments.map { newCommentItem ->
        val oldCommentItem = oldSessionFeedback?.comments?.find { it.id == newCommentItem.id }
        val newDots = if (oldCommentItem != null) {
            val diff = newCommentItem.dots.size - oldCommentItem.dots.size
            if (diff > 0) {
                oldCommentItem.dots + dots(diff, this.colors)
            } else {
                oldCommentItem.dots.dropLast(diff.absoluteValue)
            }
        } else {
            newCommentItem.dots
        }
        UIComment(
            id = newCommentItem.id,
            voteItemId = newCommentItem.voteItemId,
            message = newCommentItem.message,
            createdAt = newCommentItem.createdAt,
            upVotes = newCommentItem.upVotes,
            dots = newDots,
            votedByUser = newCommentItem.votedByUser
        )
    },
    voteItem = this.session.voteItem.map { newVoteItem ->
        val oldVoteItem = oldSessionFeedback?.voteItem?.find { it.id == newVoteItem.id }
        val newDots = if (oldVoteItem != null) {
            val diff = newVoteItem.dots.size - oldVoteItem.dots.size
            if (diff > 0) {
                oldVoteItem.dots + dots(diff, this.colors)
            } else {
                oldVoteItem.dots.dropLast(diff.absoluteValue)
            }
        } else {
            newVoteItem.dots
        }
        UIVoteItem(
            id = newVoteItem.id,
            text = newVoteItem.text,
            dots = newDots,
            votedByUser = newVoteItem.votedByUser
        )
    }
)

private fun dots(count: Int, possibleColors: List<String>): List<UIDot> = 0.until(count).map {
    UIDot(
        Random.nextFloat(),
        Random.nextFloat().coerceIn(0.1f, 0.9f),
        possibleColors[Random.nextInt().absoluteValue % possibleColors.size]
    )
}
