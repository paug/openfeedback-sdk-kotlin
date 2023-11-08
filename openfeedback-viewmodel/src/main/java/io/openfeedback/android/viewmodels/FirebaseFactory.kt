package io.openfeedback.android.viewmodels

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

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

    fun createKM(
        context: Context,
        config: OpenFeedbackFirebaseConfig,
        appName: String = "openfeedback"
    ): dev.gitlive.firebase.FirebaseApp {
        return Firebase.initialize(
            context = context,
            options = dev.gitlive.firebase.FirebaseOptions(
                projectId = config.projectId,
                applicationId = config.applicationId,
                apiKey = config.apiKey,
                databaseUrl = config.databaseUrl
            ),
            name = appName
        )
    }
}
