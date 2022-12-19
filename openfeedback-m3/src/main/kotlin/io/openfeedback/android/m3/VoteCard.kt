package io.openfeedback.android.m3

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.openfeedback.android.viewmodels.models.UIVoteItem

@ExperimentalMaterial3Api
@Composable
internal fun VoteCard(
    voteModel: UIVoteItem,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
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
        Box(modifier = Modifier.height(100.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                voteModel.dots.forEach { dot ->
                    val offset = Offset(x = this.size.width * dot.x, y = this.size.height * dot.y)
                    drawCircle(
                        color = Color(
                            dot.color.substring(0, 2).toInt(16),
                            dot.color.substring(2, 4).toInt(16),
                            dot.color.substring(4, 6).toInt(16),
                            255 / 3
                        ),
                        radius = 30.dp.value,
                        center = offset,
                        style = Fill
                    )
                }
            }
            Text(
                text = voteModel.text,
                style = style,
                color = contentColor,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}
