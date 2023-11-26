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
import io.openfeedback.viewmodels.models.UIComment
import io.openfeedback.viewmodels.models.UIDot
import io.openfeedback.viewmodels.models.UISessionFeedback
import io.openfeedback.viewmodels.models.UIVoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun OpenFeedbackLayoutPreview() {
    MaterialTheme {
        OpenFeedbackLayout(
            sessionFeedback = UISessionFeedback(
                commentValue = "",
                commentVoteItemId = "",
                comments = listOf(
                    UIComment(
                        id = "",
                        voteItemId = "",
                        message = "Nice comment",
                        createdAt = "08 August 2023",
                        upVotes = 8,
                        dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                        votedByUser = true
                    ),
                    UIComment(
                        id = "",
                        voteItemId = "",
                        message = "Another one",
                        createdAt = "08 August 2023",
                        upVotes = 0,
                        dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                        votedByUser = true
                    )
                ),
                voteItem = listOf(
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
                )
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            commentInput = {
                CommentInput(value = "", onValueChange = {}, onSubmit = {})
            },
            comment = { Comment(comment = it, onClick = {}) }
        ) {
            VoteCard(
                voteModel = it,
                onClick = {},
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        }
    }
}
