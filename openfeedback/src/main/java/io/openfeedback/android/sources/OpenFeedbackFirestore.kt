package io.openfeedback.android.sources

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.openfeedback.android.mappers.convertToModel
import io.openfeedback.android.model.Project
import io.openfeedback.android.model.SessionVotes
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
                    .filter {
                        it.data["status"] == VoteStatus.Active.value
                                && it.data["talkId"] == sessionId
                                && it.data["userId"] == userId
                    }
                    .map { it.data["voteItemId"] as String }
            }

    fun sessionVotes(projectId: String, sessionId: String): Flow<SessionVotes> =
        firestore.collection("projects/$projectId/sessionVotes")
            .document(sessionId)
            .toFlow()
            .map { querySnapshot ->
                SessionVotes(
                    votes = querySnapshot.data
                        ?.filter { it.value is Long } as? Map<String, Long>
                        ?: emptyMap(), // If there's no vote yet, default to an empty map }
                    comments = querySnapshot.data
                        ?.filter { it.value is HashMap<*, *> }
                        ?.map {
                            val voteItemId = it.key
                            (it.value as HashMap<*, *>).entries
                                .map { entry ->
                                    entry.key as String to (entry.value as Map<String, *>)
                                        .convertToModel(
                                            id = entry.key as String,
                                            voteItemId = voteItemId
                                        )
                                }
                        }
                        ?.flatten()
                        ?.associate { it.first to it.second }
                        ?: emptyMap()
                )
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

    suspend fun upVote(
        projectId: String,
        userId: String,
        talkId: String,
        voteItemId: String,
        voteId: String,
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
                    "projectId" to projectId,
                    "talkId" to talkId,
                    "voteItemId" to voteItemId,
                    "id" to documentReference.id,
                    "voteId" to voteId,
                    "createdAt" to Date(),
                    "updatedAt" to Date(),
                    "voteType" to "textPlus",
                    "userId" to userId,
                    "status" to status.value
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
