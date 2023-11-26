package io.openfeedback.viewmodels

import androidx.compose.runtime.Immutable

@Immutable
data class OpenFeedbackFirebaseConfig(
    val context: PlatformContext,
    val projectId: String,
    val applicationId: String,
    val apiKey: String,
    val databaseUrl: String,
    val appName: String = "openfeedback"
) {
    val firebaseApp = lazy { FirebaseFactory.create(config = this, appName = appName) }
}
