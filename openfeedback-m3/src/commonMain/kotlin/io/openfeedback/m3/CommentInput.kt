package io.openfeedback.m3

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import io.openfeedback.resources.LocalStrings

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
        label = { Text(text = LocalStrings.current.strings.comments.titleInput) },
        trailingIcon = {
            IconButton(onClick = onSubmit) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = LocalStrings.current.strings.comments.actionSend
                )
            }
        },
        enabled = enabled,
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        maxLines = 5
    )
}
