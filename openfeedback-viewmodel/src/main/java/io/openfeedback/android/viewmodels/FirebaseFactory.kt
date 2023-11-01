package io.openfeedback.android.viewmodels

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

internal object FirebaseFactory {
    fun create(
        context: Context,
        config: OpenFeedbackFirebaseConfig,
        appName: String = "openfeedback"
    ): FirebaseApp {
        val options = FirebaseOptions.Builder()
            .setProjectId(config.projectId)
            .setApplicationId(config.applicationId)
            .setApiKey(config.apiKey)
            .setDatabaseUrl(config.databaseUrl)
            .build()
        return FirebaseApp.initializeApp(context, options, appName)
    }
}
