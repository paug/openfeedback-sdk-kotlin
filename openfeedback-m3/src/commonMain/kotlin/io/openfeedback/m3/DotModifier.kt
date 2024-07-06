package io.openfeedback.m3

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import io.openfeedback.ui.models.UIDot

fun Modifier.drawDots(dots: List<UIDot>): Modifier = drawBehind {
    dots.forEach { dot ->
        val offset = Offset(x = this.size.width * dot.x, y = this.size.height * dot.y)
        drawCircle(
            color = Color(
                red = dot.color.substring(0, 2).toInt(16),
                green = dot.color.substring(2, 4).toInt(16),
                blue = dot.color.substring(4, 6).toInt(16),
                alpha = 255 / 3
            ),
            radius = 30.dp.value,
            center = offset,
            style = Fill
        )
    }
}
