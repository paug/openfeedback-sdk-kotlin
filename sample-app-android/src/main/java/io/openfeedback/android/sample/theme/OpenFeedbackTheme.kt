package io.openfeedback.android.sample.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun OpenFeedbackTheme(
    isLight: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isLight) dynamicLightColorScheme(context)
            else dynamicDarkColorScheme(context)
        }

        else -> if (isLight) lightColorScheme() else darkColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
