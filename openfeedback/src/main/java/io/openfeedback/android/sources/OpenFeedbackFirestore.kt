package io.openfeedback.android.sources

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.openfeedback.android.model.Project
import io.openfeedback.android.model.VoteStatus
import io.openfeedback.android.toFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date

class OpenFeedbackFirestore(
    private val firestore: FirebaseFirestore
) {
    fun project(projectId: String): Flow<Project> =
        firestore.collection("projects")
            .document(projectId)
            .toFlow()
            .map { querySnapshot ->
                querySnapshot.toObject(Project::class.java)!!
            }

    fun userVotes(projectId: String, userId: String, sessionId: String): Flow<List<String>> =
        firestore.collection("projects/$projectId/userVotes")
            .whereEqualTo("userId", userId)
            .toFlow()
            .map { querySnapshot ->
                querySnapshot
                    .filter { it.data["status"] == VoteStatus.Active.value && it.data["talkId"] == sessionId }
                    .map { it.data["voteItemId"] as String }
            }

    fun sessionVotes(projectId: String, sessionId: String): Flow<Map<String, Long>> =
        firestore.collection("projects/$projectId/sessionVotes")
            .document(sessionId)
            .toFlow()
            .map { querySnapshot ->
                querySnapshot.data as? Map<String, Long>
                    ?: emptyMap() // If there's no vote yet, default to an empty map }
            }


    suspend fun setVote(
        projectId: String,
        userId: String,
        talkId: String,
        voteItemId: String,
        status: VoteStatus
    ) {
        val collectionReference = firestore.collection("projects/$projectId/userVotes")
        val querySnapshot = collectionReference
            .whereEqualTo("userId", userId)
            .whereEqualTo("talkId", talkId)
            .whereEqualTo("voteItemId", voteItemId)
            .get()
            .await()
        if (querySnapshot.isEmpty) {
            val documentReference = collectionReference.document()
            documentReference.set(
                mapOf(
                    "id" to documentReference.id,
                    "createdAt" to Date(),
                    "projectId" to projectId,
                    "status" to status.value,
                    "talkId" to talkId,
                    "updatedAt" to Date(),
                    "userId" to userId,
                    "voteItemId" to voteItemId
                )
            )
        } else {
            collectionReference
                .document(querySnapshot.documents[0].id)
                .update(
                    mapOf(
                        "updatedAt" to Date(),
                        "status" to status.value
                    )
                )
        }
    }

    companion object Factory {
        fun create(app: FirebaseApp): OpenFeedbackFirestore {
            val firestore = FirebaseFirestore.getInstance(app)
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            return OpenFeedbackFirestore(firestore)
        }
    }
}
