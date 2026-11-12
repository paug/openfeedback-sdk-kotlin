package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.openfeedback.resources.LocalStrings

@Composable
fun CommentInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val submitComment: () -> Unit = {
        keyboardController?.hide()
        onSubmit()
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        supportingText = supportingText?.let { text ->
            {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        modifier = modifier,
        label = { Text(text = LocalStrings.current.strings.comments.titleInput) },
        trailingIcon = {
            IconButton(onClick = submitComment) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = LocalStrings.current.strings.comments.actionSend
                )
            }
        },
        enabled = enabled,
        keyboardActions = KeyboardActions(onDone = { submitComment() }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        maxLines = 5
    )
}
