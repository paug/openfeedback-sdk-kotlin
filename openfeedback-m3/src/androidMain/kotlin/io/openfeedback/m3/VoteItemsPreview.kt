package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.ui.models.UIDot
import io.openfeedback.ui.models.UIVoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VoteItemsPreview() {
    MaterialTheme {
        VoteItems(
            voteItems = listOf(
                UIVoteItem(
                    id = "",
                    text = "Fun",
                    dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                    votedByUser = true
                ),
                UIVoteItem(
                    id = "",
                    text = "Fun",
                    dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                    votedByUser = true
                )
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                VoteCard(
                    voteModel = it,
                    onClick = {},
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
            }
        )
    }
}
