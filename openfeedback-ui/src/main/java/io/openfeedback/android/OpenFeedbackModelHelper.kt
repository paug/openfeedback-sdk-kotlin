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
                    Random.nextFloat(),
                    possibleColors.get(Random.nextInt().absoluteValue % possibleColors.size)
            )
        }
    }
}

