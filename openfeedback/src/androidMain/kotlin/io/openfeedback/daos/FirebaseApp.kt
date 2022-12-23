package io.openfeedback.daos

import android.content.Context
import com.google.firebase.FirebaseOptions
import io.openfeedback.FirebaseConfig

actual typealias FirebaseApp = com.google.firebase.FirebaseApp

var appContext: Context? = null

actual fun createApp(config: FirebaseConfig): FirebaseApp {
    val options = FirebaseOptions.Builder()
        .setProjectId(config.projectId)
        .setApplicationId(config.applicationId)
        .setApiKey(config.apiKey)
        .setDatabaseUrl(config.databaseUrl)
        .build()
    return FirebaseApp.initializeApp(appContext!!, options, "openfeedback")
}
