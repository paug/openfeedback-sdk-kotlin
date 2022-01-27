package io.openfeedback.android.sample.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun OpenFeedbackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = light,
        content = content
    )
}