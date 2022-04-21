package io.openfeedback.android.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.android.OpenFeedbackConfig
import io.openfeedback.android.components.OpenFeedback
import io.openfeedback.android.components.rememberOpenFeedbackState
import io.openfeedback.android.sample.theme.OpenFeedbackTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenFeedbackTheme {
                val openFeedbackState = rememberOpenFeedbackState(
                    projectId = "mMHR63ARZQpPidFQISyc",
                    firebaseConfig = OpenFeedbackConfig.FirebaseConfig(
                        projectId = "openfeedback-b7ab9",
                        applicationId = "1:765209934800:android:a6bb09f3deabc2277297d5",
                        apiKey = "AIzaSyC_cfbh8xKwF8UPxCeasGcsHyK4s5yZFeA",
                        databaseUrl = "https://openfeedback-b7ab9.firebaseio.com"
                    )
                )
                Scaffold {
                    OpenFeedback(
                        openFeedbackState = openFeedbackState,
                        sessionId = "173222",
                        language = "en",
                        modifier = Modifier.padding(it).padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
