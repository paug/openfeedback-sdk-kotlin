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
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
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
import io.openfeedback.android.OpenFeedbackConfig
import io.openfeedback.android.m2.OpenFeedback
import io.openfeedback.android.sample.theme.DesignSystem
import io.openfeedback.android.sample.theme.OpenFeedbackTheme

class MainActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = OpenFeedbackConfig(
            context = this,
            openFeedbackProjectId = "mMHR63ARZQpPidFQISyc",
            firebaseConfig = OpenFeedbackConfig.FirebaseConfig(
                projectId = "openfeedback-b7ab9",
                applicationId = "1:765209934800:android:a6bb09f3deabc2277297d5",
                apiKey = "AIzaSyC_cfbh8xKwF8UPxCeasGcsHyK4s5yZFeA",
                databaseUrl = "https://openfeedback-b7ab9.firebaseio.com"
            )
        )
        setContent {
            var designSystem by rememberSaveable { mutableStateOf(DesignSystem.M2) }
            val isDark = isSystemInDarkTheme()
            var isLight by rememberSaveable(isDark) { mutableStateOf(isDark.not()) }
            OpenFeedbackTheme(
                designSystem = designSystem,
                isLight = isLight
            ) {
                Column {
                    ThemeSwitcher(
                        designSystem = designSystem,
                        isLight = isLight,
                        onDesignSystemChanged = { designSystem = it },
                        onLightDarkChanged = { isLight = it }
                    )
                    when (designSystem) {
                        DesignSystem.M2 -> Scaffold {
                            OpenFeedback(
                                openFeedbackState = config,
                                sessionId = "173222",
                                language = "en",
                                modifier = Modifier
                                    .padding(it)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        DesignSystem.M3 -> androidx.compose.material3.Scaffold {
                            io.openfeedback.android.m3.OpenFeedback(
                                openFeedbackState = config,
                                sessionId = "173222",
                                language = "en",
                                modifier = Modifier
                                    .padding(it)
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
    designSystem: DesignSystem,
    isLight: Boolean,
    onDesignSystemChanged: (DesignSystem) -> Unit,
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
            Text(text = "Material 2")
            Switch(checked = designSystem == DesignSystem.M3, onCheckedChange = {
                onDesignSystemChanged(
                    if (designSystem == DesignSystem.M2) DesignSystem.M3 else DesignSystem.M2
                )
            })
            Text(text = "Material 3")
        }
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
