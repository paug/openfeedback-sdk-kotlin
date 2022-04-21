package io.openfeedback.android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.android.ui.R

@Composable
internal fun PoweredBy(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body2,
    color: Color = MaterialTheme.colors.onBackground
) {
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "Powered by Openfeedback"
        },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "Powered by", style = style, color = color)
        Image(
            painter = painterResource(id = R.drawable.openfeedback),
            contentDescription = null,
            modifier = Modifier.height(style.fontSize.value.dp + 13.dp)
        )
    }
}

@Preview
@Composable
fun PoweredByPreview() {
    PoweredBy()
}
