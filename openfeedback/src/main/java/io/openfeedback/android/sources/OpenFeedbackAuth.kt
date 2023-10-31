package io.openfeedback.android.sources

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

class OpenFeedbackAuth(private val auth: FirebaseAuth) {
    suspend fun firebaseUser(): FirebaseUser? = Mutex().withLock {
        if (auth.currentUser == null) {
            val result = auth.signInAnonymously().await()
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
            OpenFeedbackAuth(FirebaseAuth.getInstance(app))
    }
}
