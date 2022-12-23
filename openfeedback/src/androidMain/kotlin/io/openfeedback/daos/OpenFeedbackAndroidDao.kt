package io.openfeedback.daos

import android.util.Log
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.openfeedback.FirebaseConfig
import io.openfeedback.OptimisticVotes
import io.openfeedback.models.Project
import io.openfeedback.models.VoteStatus
import io.openfeedback.toFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.util.Date

class OpenFeedbackAndroidDao(
    private val projectId: String,
    private val firestore: FirebaseFirestore
) : OpenFeedbackDao {
    override fun getProject(): Flow<Project> = flow {
        firestore.collection("projects")
            .document(projectId)
            .toFlow()
            .collect { documentSnapshot ->
                documentSnapshot.toObject(Project::class.java)?.let { emit(it) }
            }
    }

    override fun getUserVotes(userId: String, sessionId: String): Flow<List<String>> = flow {
        firestore.collection("projects/$projectId/userVotes")
            .whereEqualTo("userId", userId)
            .toFlow()
            .collect { querySnapshot ->
                val votes = querySnapshot
                    .filter { it.data["status"] == VoteStatus.Active.value && it.data["talkId"] == sessionId }
                    .map { it.data["voteItemId"] as String }
                emit(votes)
            }
    }

    override fun getTotalVotes(
        sessionId: String, optimisticVotes: OptimisticVotes
    ): Flow<Map<String, Long>> {
        val channel = Channel<Map<String, Long>>(Channel.CONFLATED)
        val registration = firestore.collection("projects/$projectId/sessionVotes")
            .document(sessionId)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val totalVotes = documentSnapshot!!.data as? Map<String, Long>
                    ?: emptyMap() // If there's no vote yet, default to an empty map

                optimisticVotes.lastValue = totalVotes
                channel.trySend(totalVotes)
            }

        channel.invokeOnClose {
            registration.remove()
        }

        val flow1 = flow {
            channel.consumeEach {
                emit(it)
            }
        }
        val flow2 = optimisticVotes.channel.asFlow()

        return flowOf(flow1, flow2).flattenMerge()
    }

    override fun createVote(
        userId: String, talkId: String, voteItemId: String, status: VoteStatus
    ) {
        val ref = firestore.collection("projects/$projectId/userVotes").document()
        val value: String = status.value
        ref.set(
            mapOf(
                "id" to ref.id,
                "createdAt" to Date(),
                "projectId" to projectId,
                "status" to value,
                "talkId" to talkId,
                "updatedAt" to Date(),
                "userId" to userId,
                "voteItemId" to voteItemId
            )
        )
    }

    override fun updateVote(documentId: String, status: VoteStatus) {
        firestore.collection("projects/$projectId/userVotes")
            .document(documentId).update(
                mapOf(
                    "updatedAt" to Date(),
                    "status" to status.value
                )
            )
    }

    override suspend fun documentIdOfVote(
        userId: String, talkId: String, voteItemId: String
    ): String? {
        val collectionReference = firestore.collection("projects/$projectId/userVotes")

        val querySnapshot = collectionReference
            .whereEqualTo("userId", userId)
            .whereEqualTo("talkId", talkId)
            .whereEqualTo("voteItemId", voteItemId)
            .get()
            .await()

        if (querySnapshot.size() != 1) {
            Log.e("OpenFeedbackConfig", "Too many votes registered for $userId")
        }
        if (querySnapshot.isEmpty) {
            return null
        } else {
            return querySnapshot.documents.get(0).id
        }
    }
}

actual fun createDao(projectId: String, app: FirebaseApp): OpenFeedbackDao {
    val firestore = FirebaseFirestore.getInstance(app)
    firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()
    return OpenFeedbackAndroidDao(projectId, firestore)
}
