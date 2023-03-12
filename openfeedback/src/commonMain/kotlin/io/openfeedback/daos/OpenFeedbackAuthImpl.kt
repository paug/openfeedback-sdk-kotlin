package io.openfeedback.daos

import dev.gitlive.firebase.auth.FirebaseAuth

internal class OpenFeedbackAuthImpl(
    private val auth: FirebaseAuth
) : OpenFeedbackAuth {
    override suspend fun getFirebaseUser(): String? {
        if (auth.currentUser == null) {
            val result = auth.signInAnonymously()
            if (result.user == null) {
                // TODO put a log here
                println("Cannot signInAnonymously")
            }
        }
        return auth.currentUser?.uid
    }
}
