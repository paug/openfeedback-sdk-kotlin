package io.openfeedback.viewmodels

import androidx.compose.runtime.Immutable
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.initialize

@Immutable
data class OpenFeedbackFirebaseConfig(
    val context: Any?,
    val projectId: String,
    val applicationId: String,
    val apiKey: String,
    val databaseUrl: String,
    val appName: String = "openfeedback"
) {
    companion object {
        /**
         * Returns a [OpenFeedbackFirebaseConfig] configured for the default openfeedback instance at openfeedback.io
         *
         * @param context the context on Android or null on iOS
         */
        fun default(
            context: Any?,
        ): OpenFeedbackFirebaseConfig {
            /**
             * The firebase parameters are from the openfeedback.io project so we can
             * access firestore directly
             */
            return OpenFeedbackFirebaseConfig(
                context = context,
                projectId = "open-feedback-42",
                // Hack: I replaced :web: by :ios: for the iOS SDK to behave
                applicationId = "1:635903227116:ios:31de912f8bf29befb1e1c9",
                apiKey = "AIzaSyB3ELJsaiItrln0uDGSuuHE1CfOJO67Hb4",
                databaseUrl = "https://open-feedback-42.firebaseio.com/"
            )
        }
    }
}

private val appCache = mutableMapOf<OpenFeedbackFirebaseConfig, FirebaseApp>()

fun OpenFeedbackFirebaseConfig.toFirebaseApp(): FirebaseApp {
    return appCache.getOrPut(this) {
        Firebase.initialize(
            context = context,
            options = dev.gitlive.firebase.FirebaseOptions(
                projectId = projectId,
                applicationId = applicationId,
                apiKey = apiKey,
                databaseUrl = databaseUrl
            ),
            name = appName
        )
    }
}