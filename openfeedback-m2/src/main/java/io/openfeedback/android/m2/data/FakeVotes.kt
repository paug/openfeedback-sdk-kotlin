package io.openfeedback.android.m2.data

import io.openfeedback.viewmodels.models.UIDot
import io.openfeedback.viewmodels.models.UIVoteItem
import kotlin.math.absoluteValue
import kotlin.random.Random

internal val fakeVotes = listOf(
    fakeVoteItem("Drôle/original \uD83D\uDE03", 3),
    fakeVoteItem("Trèsenrichissant enrichissant enrichissant \uD83E\uDD13", 1),
    fakeVoteItem("Super intéressant \uD83D\uDC4D", 8),
    fakeVoteItem("Très bon orateur \uD83D\uDC4F", 21),
    fakeVoteItem("Pas clair \uD83E\uDDD0", 2)
)

internal fun dots(count: Int, possibleColors: List<String>): List<UIDot> {
    return 0.until(count).map {
        UIDot(
            Random.nextFloat(),
            Random.nextFloat(),
            possibleColors[Random.nextInt().absoluteValue % possibleColors.size]
        )
    }
}

internal fun fakeVoteItem(text: String, count: Int): UIVoteItem {
    val color = listOf(
        "7cebcd",
        "0822cb",
        "d5219d",
        "8eec1a"
    )
    return UIVoteItem(
        id = Random.nextInt().toString(),
        text = text,
        dots = dots(count, color),
        votedByUser = count % 2 == 0
    )
}
