package io.openfeedback.android

import android.app.Application
import android.content.Context
import io.openfeedback.m3.OpenFeedbackInitialize
import io.openfeedback.viewmodels.OpenFeedbackFirebaseConfig

class MainApplication: Application() {
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = this
        OpenFeedbackInitialize(OpenFeedbackFirebaseConfig.default(this))
    }
}