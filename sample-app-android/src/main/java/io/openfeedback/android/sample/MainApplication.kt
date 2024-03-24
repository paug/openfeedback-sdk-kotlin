package io.openfeedback.android.sample

import android.app.Application
import io.openfeedback.viewmodels.OpenFeedbackFirebaseConfig

class MainApplication: Application() {
    lateinit var openFeedbackFirebaseConfig: OpenFeedbackFirebaseConfig

    override fun onCreate() {
        super.onCreate()
        openFeedbackFirebaseConfig = OpenFeedbackFirebaseConfig(
            context = this,
            projectId = "open-feedback-42",
            applicationId = "1:635903227116:web:31de912f8bf29befb1e1c9",
            apiKey = "AIzaSyB3ELJsaiItrln0uDGSuuHE1CfOJO67Hb4",
            databaseUrl = "https://open-feedback-42.firebaseio.com/"
        )
    }
}
