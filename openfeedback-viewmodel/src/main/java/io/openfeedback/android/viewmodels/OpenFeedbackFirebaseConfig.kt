package io.openfeedback.android.viewmodels

import android.content.Context
import androidx.compose.runtime.Immutable

@Immutable
data class OpenFeedbackFirebaseConfig(
    val context: Context,
    val projectId: String,
    val applicationId: String,
    val apiKey: String,
    val databaseUrl: String,
    val appName: String = "openfeedback"
) {
    val firebaseApp = lazy { FirebaseFactory.create(context, this, appName) }
}
