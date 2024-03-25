package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.viewmodels.models.UIVoteItem

@ExperimentalMaterial3Api
@Composable
internal fun VoteItems(
    voteItems: List<UIVoteItem>,
    modifier: Modifier = Modifier,
    columnCount: Int = 2,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    content: @Composable ColumnScope.(UIVoteItem) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        0.until(columnCount).forEach { column ->
            Column(
                verticalArrangement = verticalArrangement,
                modifier = Modifier.weight(1f)
            ) {
                voteItems
                    .filterIndexed { index, _ ->
                        index % columnCount == column
                    }
                    .forEach { voteItem ->
                        content(voteItem)
                    }
            }
        }
    }
}
