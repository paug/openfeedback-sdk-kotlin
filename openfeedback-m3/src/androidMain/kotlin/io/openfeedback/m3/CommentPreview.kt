package io.openfeedback.m3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.openfeedback.viewmodels.models.UIComment
import io.openfeedback.viewmodels.models.UIDot

@Preview
@Composable
private fun CommentPreview() {
    MaterialTheme {
        Comment(
            comment = UIComment(
                id = "",
                voteItemId = "",
                message = "Super talk and great speakers!",
                createdAt = "08 August 2023",
                upVotes = 8,
                dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                votedByUser = true
            ),
            onClick = {}
        )
    }
}
