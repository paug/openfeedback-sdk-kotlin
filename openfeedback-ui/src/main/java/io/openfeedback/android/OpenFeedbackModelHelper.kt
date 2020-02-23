package io.openfeedback.android

import io.openfeedback.android.model.*
import kotlin.math.absoluteValue
import kotlin.random.Random

object OpenFeedbackModelHelper {
    fun toUISessionFeedback(project: Project, userVotes: List<String>, totalVotes: Map<String, Long>): UISessionFeedback {

        val voteItems = project.voteItems.map { voteItem ->
            val count = totalVotes.entries.find { e ->
                voteItem.id == e.key
            }?.value
                    ?.toInt()
                    ?: 0

                UIVoteItem(
                        id = voteItem.id,
                        text = voteItem.name,
                        dots = dots(count, project.chipColors),
                        votedByUser = userVotes.contains(voteItem.id))
        }

        return UISessionFeedback(
                comments = emptyList(),
                voteItem = voteItems
        )
    }

    fun toggle(voteItem: UIVoteItem, colors: List<String>) {
        voteItem.votedByUser = !voteItem.votedByUser
        if (voteItem.votedByUser) {
            voteItem.dots = voteItem.dots + dots(1, colors)
        } else {
            voteItem.dots = voteItem.dots.dropLast(1)
        }
    }

    fun dots(count: Int, possibleColors: List<String>): List<UIDot> {
        return 0.until(count).map {
            UIDot(Random.nextFloat(),
                    Random.nextFloat().coerceIn(0.1f, 0.9f),
                    possibleColors.get(Random.nextInt().absoluteValue % possibleColors.size)
            )
        }
    }

    fun keepDotsPosition(oldSessionFeedback: UISessionFeedback?, newSessionFeedback: UISessionFeedback, colors: List<String>): UISessionFeedback {
        return UISessionFeedback(
                comments = newSessionFeedback.comments,
                voteItem = newSessionFeedback.voteItem.map { newVoteItem ->
                    val oldVoteItem = oldSessionFeedback?.voteItem?.find { it.id == newVoteItem.id }
                    val newDots = if ( oldVoteItem != null) {
                        val diff = newVoteItem.dots.size - oldVoteItem.dots.size
                        if (diff > 0) {
                            oldVoteItem.dots + dots(diff, colors)
                        } else {
                            oldVoteItem.dots.dropLast(diff.absoluteValue)
                        }
                    } else {
                        newVoteItem.dots
                    }

                    UIVoteItem(id = newVoteItem.id, text = newVoteItem.text, dots = newDots, votedByUser = newVoteItem.votedByUser)
                }
        )
    }
}

