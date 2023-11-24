package io.openfeedback.android.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.viewmodels.models.UIComment
import io.openfeedback.viewmodels.models.UIDot
import io.openfeedback.R

@Composable
internal fun CommentItems(
    comments: List<UIComment>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    commentInput: @Composable ColumnScope.() -> Unit,
    comment: @Composable ColumnScope.(UIComment) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        Text(
            text = stringResource(id = R.string.openfeedback_comments_title),
            style = MaterialTheme.typography.titleMedium
        )
        commentInput()
        comments.forEachIndexed { index, uiComment ->
            comment(uiComment)
        }
    }
}

@Preview
@Composable
private fun VoteItemsPreview() {
    MaterialTheme {
        CommentItems(
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
                    message = "Another comment",
                    createdAt = "08 August 2023",
                    upVotes = 0,
                    dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                    votedByUser = true
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
