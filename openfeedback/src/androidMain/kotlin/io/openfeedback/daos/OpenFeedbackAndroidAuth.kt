package io.openfeedback.daos

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class OpenFeedbackAndroidAuth(
    private val auth: FirebaseAuth
) : OpenFeedbackAuth {
    override suspend fun getFirebaseUser(): String? {
        if (auth.currentUser == null) {
            val result = auth.signInAnonymously().await()
            if (result.user == null) {
                Log.e("OpenFeedbackConfig", "Cannot signInAnonymously")
            }
        }
        return auth.currentUser?.uid
    }
}

actual fun createAuth(app: FirebaseApp): OpenFeedbackAuth =
    OpenFeedbackAndroidAuth(FirebaseAuth.getInstance(app))
