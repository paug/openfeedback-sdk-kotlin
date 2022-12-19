package io.openfeedback.android.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.android.viewmodels.models.UIVoteItem

@ExperimentalMaterial3Api
@Composable
internal fun VoteItems(
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
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}
