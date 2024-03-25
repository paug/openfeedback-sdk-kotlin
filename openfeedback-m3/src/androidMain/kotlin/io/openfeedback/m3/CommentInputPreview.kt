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
