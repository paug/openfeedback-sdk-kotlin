package io.openfeedback.android.sample

import android.app.Application
import io.openfeedback.android.OpenFeedback

class MainApplication : Application() {
    lateinit var openFeedback: OpenFeedback

    override fun onCreate() {
        super.onCreate()
        openFeedback = OpenFeedback(context = this,
                openFeedbackProjectId = "Y7J4MXql8rSR2NUPhPuy",
                firebaseConfig = OpenFeedback.FirebaseConfig(
                        projectId = "openfeedback-b7ab9",
                        applicationId = "1:765209934800:android:a6bb09f3deabc2277297d5",
                        apiKey = "AIzaSyC_cfbh8xKwF8UPxCeasGcsHyK4s5yZFeA",
                        databaseUrl = "https://openfeedback-b7ab9.firebaseio.com"
                )
        )
    }
}