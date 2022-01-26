package io.openfeedback.android.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openfeedback.android.components.SessionFeedbackContainer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val openFeedback = (applicationContext as MainApplication).openFeedback
        setContent {
            Scaffold {
                SessionFeedbackContainer(
                    openFeedback = openFeedback,
                    sessionId = "173222",
                    language = "en",
                    modifier = Modifier.padding(it).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
