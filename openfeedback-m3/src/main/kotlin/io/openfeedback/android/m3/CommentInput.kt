package io.openfeedback.android.m3

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import io.openfeedback.R

@Composable
fun CommentInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(text = stringResource(id = R.string.openfeedback_comments_title_input)) },
        trailingIcon = {
            IconButton(onClick = onSubmit) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = stringResource(id = R.string.openfeedback_comments_send)
                )
            }
        },
        enabled = enabled,
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        maxLines = 5
    )
}

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
