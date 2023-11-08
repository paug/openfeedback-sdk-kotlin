package io.openfeedback.android.sources

import android.util.Log
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OpenFeedbackAuth(private val auth: FirebaseAuth) {
    suspend fun firebaseUser(): FirebaseUser? = Mutex().withLock {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
            val result = auth.signInAnonymously()
            if (result.user == null) {
                Log.e("OpenFeedbackAuth", "Cannot signInAnonymously")
            }
        }
        auth.currentUser
    }

    suspend fun <R> withFirebaseUser(block: suspend (FirebaseUser) -> R?): R? {
        return firebaseUser()?.let { block.invoke(it) }
    }

    companion object Factory {
        fun create(app: FirebaseApp): OpenFeedbackAuth =
            OpenFeedbackAuth(Firebase.auth(app))
    }
}
