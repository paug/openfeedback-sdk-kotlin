package io.openfeedback.android.sources

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.openfeedback.android.FirebaseConfig

object FirebaseFactory {
    fun create(
        context: Context,
        config: FirebaseConfig,
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
