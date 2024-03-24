package io.openfeedback.android.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.android.sample.theme.OpenFeedbackTheme
import io.openfeedback.m3.OpenFeedback

class MainActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val projectId = "x957vfwwtWxn8wF6RSLb"
        val sessionId = "KWhBK2ysHnazBasuU7s0"
        val openFeedbackFirebaseConfig = (application as MainApplication).openFeedbackFirebaseConfig
        setContent {
            val isDark = isSystemInDarkTheme()
            var isLight by rememberSaveable(isDark) { mutableStateOf(isDark.not()) }
            OpenFeedbackTheme(
                isLight = isLight
            ) {
                Scaffold {
                    LazyColumn(contentPadding = it) {
                        item {
                            ThemeSwitcher(isLight = isLight) { isLight = it }
                        }
                        item {
                            OpenFeedback(
                                config = openFeedbackFirebaseConfig,
                                projectId = projectId,
                                sessionId = sessionId,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
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
