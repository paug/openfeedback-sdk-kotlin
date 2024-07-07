package io.openfeedback.resources

import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.lyricist.Lyricist

val LocalStrings = staticCompositionLocalOf {
    Lyricist(
        defaultLanguageTag = "en",
        translations = mapOf("en" to EnStrings, "fr" to FrStrings)
    )
}
