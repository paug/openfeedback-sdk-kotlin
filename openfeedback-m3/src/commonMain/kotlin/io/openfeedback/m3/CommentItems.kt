package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.openfeedback.MR
import io.openfeedback.viewmodels.models.UIComment

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
            text = stringResource(MR.strings.openfeedback_comments_title),
            style = MaterialTheme.typography.titleMedium
        )
        commentInput()
        comments.forEach { uiComment ->
            comment(uiComment)
        }
    }
}
