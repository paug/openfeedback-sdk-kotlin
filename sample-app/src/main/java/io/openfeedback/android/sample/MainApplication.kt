package io.openfeedback.android.sample

import android.app.Application
import io.openfeedback.viewmodels.OpenFeedbackFirebaseConfig

class MainApplication: Application() {
    lateinit var openFeedbackFirebaseConfig: OpenFeedbackFirebaseConfig

    override fun onCreate() {
        super.onCreate()
        openFeedbackFirebaseConfig = OpenFeedbackFirebaseConfig(
            context = this,
            projectId = "openfeedback-b7ab9",
            applicationId = "1:765209934800:android:a6bb09f3deabc2277297d5",
            apiKey = "AIzaSyC_cfbh8xKwF8UPxCeasGcsHyK4s5yZFeA",
            databaseUrl = "https://openfeedback-b7ab9.firebaseio.com"
        )
    }
}
