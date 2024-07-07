package io.openfeedback.m3

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.openfeedback.resources.LocalStrings
import io.openfeedback.ui.models.UIComment

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
                    text = LocalStrings.current.strings.comments.nbVotes(comment.upVotes),
                    color = contentColor.copy(alpha = .7f),
                    style = subStyle
                )
                Text(
                    text = comment.createdAt + (if (comment.fromUser) LocalStrings.current.strings.fromYou else ""),
                    color = contentColor.copy(alpha = .7f),
                    style = subStyle
                )
            }
        }
    }
}
