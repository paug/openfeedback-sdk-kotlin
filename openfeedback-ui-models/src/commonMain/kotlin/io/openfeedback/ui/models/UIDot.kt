package io.openfeedback.ui.models

import androidx.compose.runtime.Immutable

/**
 * @param x: the x coordinate between 0f and 1f
 * @param y: the y coordinate between 0f and 1f
 * @param color: the color as "rrggbb"
 */
@Immutable
data class UIDot(
    val x: Float,
    val y: Float,
    val color: String
)
