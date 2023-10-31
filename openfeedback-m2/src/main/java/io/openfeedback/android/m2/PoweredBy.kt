package io.openfeedback.android.m2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openfeedback.android.R as ROF

@Composable
fun PoweredBy(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body2,
    color: Color = MaterialTheme.colors.onBackground
) {
    val logo =
        if (MaterialTheme.colors.isLight) ROF.drawable.openfeedback_light
        else ROF.drawable.openfeedback_dark
    val poweredBy = stringResource(id = ROF.string.powered_by)
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "$poweredBy Openfeedback"
        },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = poweredBy, style = style, color = color)
        Image(
            painter = painterResource(id = logo),
            contentDescription = null,
            modifier = Modifier.height(style.fontSize.value.dp + 13.dp)
        )
    }
}

@Preview
@Composable
internal fun PoweredByPreview() {
    Column {
        MaterialTheme {
            PoweredBy()
        }
        MaterialTheme(colors = darkColors()) {
            PoweredBy()
        }
    }
}
