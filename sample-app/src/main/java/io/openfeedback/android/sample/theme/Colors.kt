package io.openfeedback.android.sample.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

object ColorPalette {
    val black = Color(52, 52, 52)
    val greyLight = Color(237, 237, 237)
    val white = Color.White
}

val light = lightColors(
    background = ColorPalette.white,
    onBackground = ColorPalette.black,
    surface = ColorPalette.greyLight,
    onSurface = ColorPalette.black
)
