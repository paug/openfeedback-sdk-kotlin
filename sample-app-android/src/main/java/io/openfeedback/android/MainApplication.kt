package io.openfeedback.android

import android.app.Application
import android.content.Context

class MainApplication: Application() {
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}