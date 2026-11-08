package io.openfeedback.m3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun CommentInputPreview() {
    MaterialTheme {
        CommentInput(
            value = "My comment",
            onValueChange = {},
            onSubmit = {}
        )
    }
}

@Preview
@Composable
private fun CommentInputHiddenPreview() {
    MaterialTheme {
        CommentInput(
            value = "My hidden comment",
            onValueChange = {},
            onSubmit = {},
            supportingText = "Hidden by the event organizer. Contact them for more information."
        )
    }
}
