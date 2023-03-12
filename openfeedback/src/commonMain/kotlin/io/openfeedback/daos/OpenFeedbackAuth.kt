package io.openfeedback.daos

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.auth.auth

interface OpenFeedbackAuth {
    suspend fun getFirebaseUser(): String?

    object Factory {
        fun createAuth(app: FirebaseApp): OpenFeedbackAuth =
            OpenFeedbackAuthImpl(Firebase.auth(app))
    }
}
