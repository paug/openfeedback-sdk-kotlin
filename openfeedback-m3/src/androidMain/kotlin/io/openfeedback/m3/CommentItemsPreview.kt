package io.openfeedback.m3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.openfeedback.ui.models.UIComment
import io.openfeedback.ui.models.UIDot
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
private fun CommentItemsPreview() {
    MaterialTheme {
        CommentItems(
            comments = persistentListOf(
                UIComment(
                    id = "",
                    message = "Nice comment",
                    createdAt = "08 August 2023",
                    upVotes = 8,
                    dots = persistentListOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                    votedByUser = true,
                    fromUser = false
                ),
                UIComment(
                    id = "",
                    message = "Another comment",
                    createdAt = "08 August 2023",
                    upVotes = 0,
                    dots = persistentListOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
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
