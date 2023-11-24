package io.openfeedback.viewmodels

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

internal object FirebaseFactory {
    fun create(
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
