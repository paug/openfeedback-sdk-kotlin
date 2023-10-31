package io.openfeedback.android.viewmodels.mappers

import io.openfeedback.android.model.Project
import io.openfeedback.android.viewmodels.models.UIDot
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UISessionFeedbackWithColors
import io.openfeedback.android.viewmodels.models.UIVoteItem
import kotlin.math.absoluteValue
import kotlin.random.Random

fun convertToUiSessionFeedback(
    project: Project,
    userVotes: List<String>,
    totalVotes: Map<String, Long>,
    language: String
): UISessionFeedback = UISessionFeedback(
    comments = emptyList(),
    voteItem = project.voteItems
        .filter { it.type == "boolean" }
        .map { voteItem ->
            val count = totalVotes.entries
                .find { e -> voteItem.id == e.key }
                ?.value
                ?.toInt()
                ?: 0
            UIVoteItem(
                id = voteItem.id,
                text = voteItem.localizedName(language),
                dots = dots(count, project.chipColors),
                votedByUser = userVotes.contains(voteItem.id)
            )
        }
)

fun UISessionFeedbackWithColors.convertToUiSessionFeedback(
    oldSessionFeedback: UISessionFeedback?
): UISessionFeedback = UISessionFeedback(
    comments = this.session.comments,
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
