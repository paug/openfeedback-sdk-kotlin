package io.openfeedback.android.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.android.model.UIDot
import io.openfeedback.android.model.UIVoteItem
import kotlin.math.absoluteValue
import kotlin.random.Random

@Composable
fun VoteItems(
    voteItems: List<UIVoteItem>,
    modifier: Modifier = Modifier,
    columnCount: Int = 2,
    onClick: (voteItem: UIVoteItem) -> Unit
) {
    val spaceGrid = 8.dp
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spaceGrid)
    ) {
        0.until(columnCount).forEach { column ->
            Box(modifier = Modifier.weight(1f)) {
                Column(verticalArrangement = Arrangement.spacedBy(spaceGrid)) {
                    voteItems.filterIndexed { index, _ ->
                        index % columnCount == column
                    }.forEach { voteItem ->
                        VoteCard(
                            voteModel = voteItem,
                            modifier = Modifier.clickable(
                                role = Role.RadioButton,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { onClick(voteItem) }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun VoteItemsPreview() {
    VoteItems(voteItems = fakeVotes, columnCount = 2) {}
}

val fakeVotes = listOf(
    fakeVoteItem("Drôle/original \uD83D\uDE03", 3),
    fakeVoteItem("Trèsenrichissant enrichissant enrichissant \uD83E\uDD13", 1),
    fakeVoteItem("Super intéressant \uD83D\uDC4D", 8),
    fakeVoteItem("Très bon orateur \uD83D\uDC4F", 21),
    fakeVoteItem("Pas clair \uD83E\uDDD0", 2)
)

fun dots(count: Int, possibleColors: List<String>): List<UIDot> {
    return 0.until(count).map {
        UIDot(
            Random.nextFloat(),
            Random.nextFloat(),
            possibleColors.get(Random.nextInt().absoluteValue % possibleColors.size)
        )
    }
}

fun fakeVoteItem(text: String, count: Int): UIVoteItem {
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
