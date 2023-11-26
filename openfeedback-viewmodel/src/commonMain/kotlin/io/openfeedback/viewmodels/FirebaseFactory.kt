package io.openfeedback.viewmodels

import dev.gitlive.firebase.FirebaseApp

internal expect object FirebaseFactory {
    fun create(config: OpenFeedbackFirebaseConfig, appName: String = "openfeedback"): FirebaseApp
}
