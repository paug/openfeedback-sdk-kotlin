import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.OpenFeedback

@Composable
fun SampleApp(
    isSystemLight: Boolean,
    context: Any?,
) {
    var isLight by rememberSaveable(isSystemLight) { mutableStateOf(isSystemLight) }
    OpenFeedbackTheme(
        isLight = isLight
    ) {
        Scaffold {
            LazyColumn(contentPadding = it) {
                item {
                    ThemeSwitcher(isLight = isLight) { isLight = it }
                }
                item {
                    /**
                     * The project and session Ids are taken from openfeedback.io demo conference:
                     * https://openfeedback.io/eaJnyMXD3oNfhrrnBYDT/
                     */
                    OpenFeedback(
                        projectId = "eaJnyMXD3oNfhrrnBYDT",
                        sessionId = "100",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        displayComments = true
                    )
                }
            }
        }
    }
}

@Composable
fun OpenFeedbackTheme(
    isLight: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        else -> if (isLight) lightColorScheme() else darkColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun ThemeSwitcher(
    isLight: Boolean,
    onLightDarkChanged: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Dark")
            Switch(checked = isLight, onCheckedChange = {
                onLightDarkChanged(!isLight)
            })
            Text(text = "Light")
        }
    }
}
