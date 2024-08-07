package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.resources.LocalStrings
import io.openfeedback.ui.models.UIComment
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun CommentItems(
    comments: ImmutableList<UIComment>,
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
            text = LocalStrings.current.strings.comments.titleSection,
            style = MaterialTheme.typography.titleMedium
        )
        commentInput()
        comments.forEach { uiComment ->
            comment(uiComment)
        }
    }
}
