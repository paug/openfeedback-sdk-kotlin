package io.openfeedback.sources

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OpenFeedbackAuth(app: FirebaseApp) {
    private val auth = Firebase.auth(app)
    private val mutex = Mutex()

    suspend fun userId(): String {
        mutex.withLock {
            // TODO: move this to a one-time initialization at startup
            if (auth.currentUser == null) {
                auth.signInAnonymously()
                val result = auth.signInAnonymously()
                if (result.user == null) {
                    Logger.e("OpenFeedbackAuth") { "Cannot signInAnonymously" }
                }
            }
        }
        return auth.currentUser?.uid ?: "woopsie"
    }
}
