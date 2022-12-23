package io.openfeedback.daos

interface OpenFeedbackAuth {
    suspend fun getFirebaseUser(): String?
}

expect fun createAuth(app: FirebaseApp): OpenFeedbackAuth
