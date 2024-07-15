package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.openfeedback.resources.LocalStrings

@Composable
fun FeedbackNotReady(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = contentColorFor(containerColor),
    contentPadding: PaddingValues = PaddingValues(32.dp)
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CompositionLocalProvider(LocalContentColor provides contentColor.copy(alpha = 0.38f)) {
                        Icon(
                            imageVector = Icons.Default.SentimentVerySatisfied,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfied,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.SentimentNeutral,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.SentimentDissatisfied,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.SentimentVeryDissatisfied,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = LocalStrings.current.strings.notReady.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = LocalStrings.current.strings.notReady.description)
        }
    }
}
