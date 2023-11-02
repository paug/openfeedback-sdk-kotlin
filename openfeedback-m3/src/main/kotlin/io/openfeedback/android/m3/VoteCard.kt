package io.openfeedback.android.m3

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.android.viewmodels.models.UIDot
import io.openfeedback.android.viewmodels.models.UIVoteItem

@ExperimentalMaterial3Api
@Composable
fun VoteCard(
    voteModel: UIVoteItem,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    shape: Shape = MaterialTheme.shapes.medium,
    onClick: (voteItem: UIVoteItem) -> Unit
) {
    val border = if (voteModel.votedByUser) 4.dp else 1.dp
    Surface(
        shape = shape,
        border = BorderStroke(border, contentColor.copy(alpha = .2f)),
        color = backgroundColor,
        modifier = modifier,
        onClick = { onClick(voteModel) }
    ) {
        Box(
            modifier = Modifier.drawDots(voteModel.dots)
        ) {
            Text(
                text = voteModel.text,
                style = style,
                color = contentColor,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun VoteCardPreview() {
    MaterialTheme {
        VoteCard(
            voteModel = UIVoteItem(
                id = "",
                text = "Fun",
                dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                votedByUser = true
            ),
            onClick = {},
            modifier = Modifier.size(height = 100.dp, width = 200.dp)
        )
    }
}
