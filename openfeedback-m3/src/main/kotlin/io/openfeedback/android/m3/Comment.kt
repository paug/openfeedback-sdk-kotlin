package io.openfeedback.android.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.R
import io.openfeedback.android.viewmodels.models.UIComment
import io.openfeedback.android.viewmodels.models.UIDot

@Composable
fun Comment(
    comment: UIComment,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = contentColorFor(backgroundColor = containerColor),
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    subStyle: TextStyle = MaterialTheme.typography.labelMedium,
    shape: Shape = MaterialTheme.shapes.medium,
    onClick: (UIComment) -> Unit
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        modifier = modifier,
        onClick = { onClick(comment) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawDots(comment.dots)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = comment.message, style = style)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        id = R.string.openfeedback_comments_upvotes,
                        comment.upVotes
                    ),
                    color = contentColor.copy(alpha = .7f),
                    style = subStyle
                )
                Text(
                    text = comment.createdAt,
                    color = contentColor.copy(alpha = .7f),
                    style = subStyle
                )
            }
        }
    }
}

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
