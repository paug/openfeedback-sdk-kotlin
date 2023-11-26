package io.openfeedback.m3

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.viewmodels.models.UIDot
import io.openfeedback.viewmodels.models.UIVoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VoteCardPreview() {
    MaterialTheme {
        VoteCard(
            voteModel = UIVoteItem(
                id = "",
                text = "Fun",
                dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                votedByUser = true
            ),
            onClick = {},
            modifier = Modifier.size(height = 100.dp, width = 200.dp)
        )
    }
}
