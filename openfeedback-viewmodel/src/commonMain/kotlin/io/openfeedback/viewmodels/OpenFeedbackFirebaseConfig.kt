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
)

fun OpenFeedbackFirebaseConfig.toFirebaseApp(): FirebaseApp = Firebase.initialize(
    context = context,
    options = dev.gitlive.firebase.FirebaseOptions(
        projectId = projectId,
        applicationId = applicationId,
        apiKey = apiKey,
        databaseUrl = databaseUrl
    ),
    name = appName
)