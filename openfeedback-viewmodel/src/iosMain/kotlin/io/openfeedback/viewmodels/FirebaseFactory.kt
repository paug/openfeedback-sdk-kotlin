package io.openfeedback.viewmodels

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.initialize

internal actual object FirebaseFactory {
    actual fun create(config: OpenFeedbackFirebaseConfig, appName: String): FirebaseApp =
        Firebase.initialize(
            context = null,
            options = dev.gitlive.firebase.FirebaseOptions(
                projectId = config.projectId,
                applicationId = config.applicationId,
                apiKey = config.apiKey,
                databaseUrl = config.databaseUrl
            ),
            name = appName
        )
}
