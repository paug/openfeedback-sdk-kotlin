package io.openfeedback.android

import android.app.Application
import android.content.Context
import io.openfeedback.m3.initializeOpenFeedback
import io.openfeedback.viewmodels.OpenFeedbackFirebaseConfig

class MainApplication: Application() {
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = this
        initializeOpenFeedback(OpenFeedbackFirebaseConfig.default(this))
    }
}