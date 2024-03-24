package io.openfeedback.shared

import SampleApp
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    SampleApp(
        true,
        null
    )
}