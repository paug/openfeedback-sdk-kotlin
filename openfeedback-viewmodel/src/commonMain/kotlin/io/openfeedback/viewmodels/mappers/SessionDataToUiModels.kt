package io.openfeedback.viewmodels.mappers

import com.vanniktech.locale.Locale
import io.openfeedback.model.SessionData
import io.openfeedback.ui.models.UIComment
import io.openfeedback.ui.models.UIDot
import io.openfeedback.ui.models.UISessionFeedback
import io.openfeedback.ui.models.UIVoteItem
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * Map [SessionData] instance to [UISessionFeedback], stable model for Compose UI.
 *
 * @param locale User locale.
 * @param oldVoteItems Old version of vote items.
 * @param oldComments old version of comments.
 * @return [UISessionFeedback] model.
 */
internal fun SessionData.toUISessionFeedback(
    locale: Locale,
    oldVoteItems: List<UIVoteItem>?,
    oldComments: List<UIComment>?,
): UISessionFeedback {
    val sessionData = this
    val votedItemIds = sessionData.votedItemIds
    return UISessionFeedback(
        voteItems = sessionData.project.voteItems
            .filter { it.type == "boolean" }
            .map { voteItem ->
                val oldVoteItem = oldVoteItems?.firstOrNull { it.id == voteItem.id }
                val count = sessionData.voteItemAggregates[voteItem.id]?.toInt() ?: 0
                val oldDots = oldVoteItem?.dots.orEmpty()
                val diff = count - oldDots.size
                val dots = if (diff > 0) {
                    oldDots + newDots(diff, sessionData.project.chipColors)
                } else {
                    oldDots.dropLast(diff.absoluteValue)
                }
                UIVoteItem(
                    id = voteItem.id,
                    text = voteItem.localizedName(locale.language.code),
                    dots = dots,
                    votedByUser = votedItemIds.contains(voteItem.id)
                )
            },
        comments = sessionData.comments.map { commentItem ->
            val localDateTime =
                commentItem.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            val oldComment = oldComments?.firstOrNull { it.id == commentItem.id }
            val oldDots = oldComment?.dots.orEmpty()
            val diff = commentItem.plus.toInt() - oldDots.size
            val dots = if (diff > 0) {
                oldDots + newDots(diff, sessionData.project.chipColors)
            } else {
                oldDots.dropLast(diff.absoluteValue)
            }
            UIComment(
                id = commentItem.id,
                message = commentItem.text,
                createdAt = localDateTime.format(dateFormat),
                upVotes = commentItem.plus.toInt(),
                dots = dots,
                votedByUser = sessionData.votedCommentIds.contains(commentItem.id),
                fromUser = commentItem.userId == sessionData.userId
            )
        },
        colors = sessionData.project.chipColors
    )
}

/**
 * Compute new version of dots.
 *
 * @param count Number of dots to generate.
 * @param possibleColors Possible colors to display.
 * @return List of [UIDot] model.
 */
private fun newDots(count: Int, possibleColors: List<String>): List<UIDot> = 0.until(count).map {
    UIDot(
        Random.nextFloat(),
        Random.nextFloat().coerceIn(0.1f, 0.9f),
        possibleColors[Random.nextInt().absoluteValue % possibleColors.size]
    )
}

private val dateFormat = LocalDateTime.Format {
    dayOfMonth()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    chars(", ")
    hour()
    char(':')
    minute()
}
