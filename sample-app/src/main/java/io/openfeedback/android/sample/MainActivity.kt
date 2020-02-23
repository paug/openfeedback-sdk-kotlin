package io.openfeedback.android.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import io.openfeedback.android.OpenFeedback
import io.openfeedback.android.compose.SessionFeedbackContainer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val openFeedback = OpenFeedback(context = this,
                openFeedbackProjectId = "aeHBMV63ZwR4gdsUpTmS",
                firebaseConfig = OpenFeedback.FirebaseConfig(
                        projectId = "openfeedbackandroid",
                        applicationId = "1:374468031823:web:1c09ba872a0b0b1439013a",
                        apiKey = "AIzaSyBz84579hY2Ry_lnNBqcfD2D4fXwx3g5V4",
                        databaseUrl = "https://openfeedbackandroid.firebaseio.com"
                )
        )
        setContent {
            SessionFeedbackContainer(openFeedback, "xtrlxKpGxidZciFN5EbS")
        }
    }
}
