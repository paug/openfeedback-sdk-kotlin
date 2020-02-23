package io.openfeedback.android

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.openfeedback.android.model.Project
import io.openfeedback.android.model.Snapshot
import io.openfeedback.android.model.VoteStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.util.*

class OpenFeedback(context: Context,
                   firebaseConfig: FirebaseConfig,
                   val openFeedbackProjectId: String,
                   appName: String = "openfeedback") {

    val firestore: FirebaseFirestore
    val auth: FirebaseAuth

    class FirebaseConfig(
            val projectId: String,
            val applicationId: String,
            val apiKey: String,
            val databaseUrl: String
    )
    init {
        val options = FirebaseOptions.Builder()
                .setProjectId(firebaseConfig.projectId)
                .setApplicationId(firebaseConfig.applicationId)
                .setApiKey(firebaseConfig.apiKey)
                .setDatabaseUrl(firebaseConfig.databaseUrl)
                .build()

        val app = FirebaseApp.initializeApp(context, options, appName)

        firestore = FirebaseFirestore.getInstance(app)

        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()

        auth = FirebaseAuth.getInstance(app)
    }

    private suspend fun <R> withFirebaseUser(block: suspend (FirebaseUser) -> R?): R? {
        return getFirebaseUser()?.let { block.invoke(it) }
    }

    private suspend fun getFirebaseUser() = Mutex().withLock {
        if (auth.currentUser == null) {
            val result = auth.signInAnonymously().await()
            if (result.user == null) {
                Log.e(OpenFeedback::class.java.name, "Cannot signInAnonymously")
            }
        }
        auth.currentUser
    }

    suspend fun getProject(): Flow<Snapshot<Project?>> = flow {
        val task = firestore.collection("projects")
                .document(openFeedbackProjectId)
                .toFlow()
                .collect {documentSnapshot ->
                    val snapshot = Snapshot(documentSnapshot.toObject(Project::class.java),
                            documentSnapshot.metadata.isFromCache)

                    emit(snapshot)
                }
    }

    fun getUserVotes(sessionId: String) = flow {
        val user = getFirebaseUser()
        if (user != null) {
            firestore.collection("projects/$openFeedbackProjectId/userVotes")
                    .whereEqualTo("userId", user.uid)
                    .toFlow()
                    .collect {querySnapshot ->
                        val votes = querySnapshot.filter {
                            it.data.get("status") == VoteStatus.Active.value
                                    && it.data.get("talkId") == sessionId
                        }.map {
                            it.data.get("voteItemId") as String
                        }
                        val snapshot = Snapshot(votes, querySnapshot.metadata.isFromCache)
                        emit(snapshot)
                    }
        }
    }

    fun getTotalVotes(sessionId: String): Flow<Snapshot<Map<String, Long>>> = flow {
        val user = getFirebaseUser()
        if (user != null) {
            firestore.collection("projects/$openFeedbackProjectId/sessionVotes")
                    .toFlow()
                    .collect { querySnapshot ->
                        // Somehow I can query the list but not a single document..
                        // Fix this one day
                        val totalVotes = querySnapshot
                                .firstOrNull { it.id == sessionId }
                                ?.data as? Map<String, Long>
                                ?: emptyMap() // If there's no vote yet, default to an empty map
                        val snapshot = Snapshot(totalVotes, querySnapshot.metadata.isFromCache)
                        emit(snapshot)
                    }
        }
    }

    suspend fun setVote(talkId: String, voteItemId: String, status: VoteStatus) = withFirebaseUser { firebaseUser ->
        val collectionReference = firestore.collection("projects/$openFeedbackProjectId/userVotes")
        val querySnapshot = collectionReference
                .whereEqualTo("userId", firebaseUser.uid)
                .whereEqualTo("talkId", talkId)
                .whereEqualTo("voteItemId", voteItemId)
                .get()
                .await()

        if (querySnapshot.isEmpty) {
            val documentReference = collectionReference.document()
            documentReference.set(
                    mapOf("id" to documentReference.id,
                            "createdAt" to Date(),
                            "projectId" to openFeedbackProjectId,
                            "status" to status.value,
                            "talkId" to talkId,
                            "updatedAt" to Date(),
                            "userId" to firebaseUser.uid,
                            "voteItemId" to voteItemId)
            )
        } else {
            if (querySnapshot.size() != 1) {
                Log.e(OpenFeedback::class.java.name, "Too many votes registered for ${firebaseUser.uid}")
            }

            val documentID = querySnapshot.documents.get(0).id
            collectionReference.document(documentID).update(
                    mapOf("updatedAt" to Date(),
                            "status" to status.value)
            )
        }
    }
}

