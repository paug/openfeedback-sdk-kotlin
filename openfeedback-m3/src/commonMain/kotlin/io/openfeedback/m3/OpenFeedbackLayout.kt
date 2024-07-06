package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.ui.models.UIComment
import io.openfeedback.ui.models.UISessionFeedback
import io.openfeedback.ui.models.UIVoteItem

/**
 * Stateless OpenFeedback component to display vote items, text field to enter a new comment
 * and display comments of a session.
 *
 * @param sessionFeedback Ui model for vote items, new comment value and comments.
 * @param modifier The modifier to be applied to the component.
 * @param columnCount Number of column to display for vote items.
 * @param horizontalArrangement The horizontal arrangement of the vote items layout.
 * @param verticalArrangement The vertical arrangement to display between every column.
 * @param comment API slot for the list of comments.
 * @param commentInput API slot for the text field to create new comment.
 * @param voteItem API slot for vote items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFeedbackLayout(
    sessionFeedback: UISessionFeedback,
    modifier: Modifier = Modifier,
    columnCount: Int = 2,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    comment: @Composable ColumnScope.(UIComment) -> Unit,
    commentInput: @Composable ColumnScope.() -> Unit,
    voteItem: @Composable ColumnScope.(UIVoteItem) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        VoteItems(
            voteItems = sessionFeedback.voteItems,
            columnCount = columnCount,
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            content = voteItem
        )
        if (sessionFeedback.comments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            CommentItems(
                comments = sessionFeedback.comments,
                verticalArrangement = verticalArrangement,
                commentInput = commentInput,
                comment = comment
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PoweredBy()
        }
    }
}
