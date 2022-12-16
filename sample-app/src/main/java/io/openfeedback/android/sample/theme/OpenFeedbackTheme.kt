package io.openfeedback.android.sample.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class DesignSystem {
    M2, M3
}

@Composable
fun OpenFeedbackTheme(
    designSystem: DesignSystem,
    isLight: Boolean,
    content: @Composable () -> Unit
) {
    when (designSystem) {
        DesignSystem.M2 -> MaterialTheme(
            colors = if (isLight) light else darkColors(),
            content = content
        )
        DesignSystem.M3 -> {
            val colorScheme = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (isLight) dynamicLightColorScheme(context)
                    else dynamicDarkColorScheme(context)
                }

                else -> if (isLight) lightColorScheme() else darkColorScheme()
            }
            androidx.compose.material3.MaterialTheme(
                colorScheme = colorScheme,
                content = content
            )
        }
    }
}
