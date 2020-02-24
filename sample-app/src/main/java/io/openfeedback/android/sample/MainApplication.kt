package io.openfeedback.android.sample

import android.app.Application
import io.openfeedback.android.OpenFeedback

class MainApplication : Application() {
    lateinit var openFeedback: OpenFeedback

    override fun onCreate() {
        super.onCreate()
        openFeedback = OpenFeedback(context = this,
                openFeedbackProjectId = "7Hq01JIxGJtCQ7bRGIYN",
                firebaseConfig = OpenFeedback.FirebaseConfig(
                        projectId = "openfeedbackandroid",
                        applicationId = "1:374468031823:web:1c09ba872a0b0b1439013a",
                        apiKey = "AIzaSyBz84579hY2Ry_lnNBqcfD2D4fXwx3g5V4",
                        databaseUrl = "https://openfeedbackandroid.firebaseio.com"
                )
        )
    }
}