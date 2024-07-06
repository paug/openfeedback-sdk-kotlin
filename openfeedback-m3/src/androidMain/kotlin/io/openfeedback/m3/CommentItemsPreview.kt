package io.openfeedback.m3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.openfeedback.ui.models.UIComment
import io.openfeedback.ui.models.UIDot

@Preview
@Composable
private fun CommentItemsPreview() {
    MaterialTheme {
        CommentItems(
            comments = listOf(
                UIComment(
                    id = "",
                    message = "Nice comment",
                    createdAt = "08 August 2023",
                    upVotes = 8,
                    dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                    votedByUser = true,
                    fromUser = false
                ),
                UIComment(
                    id = "",
                    message = "Another comment",
                    createdAt = "08 August 2023",
                    upVotes = 0,
                    dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                    votedByUser = true,
                    fromUser = false
                )
            ),
            commentInput = {
                CommentInput(value = "", onValueChange = {}, onSubmit = {})
            },
            comment = {
                Comment(comment = it, onClick = {})
            }
        )
    }
}
