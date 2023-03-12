package io.openfeedback.daos

import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import io.openfeedback.serializers.SessionVoteDeserializer
import io.openfeedback.OptimisticVotes
import io.openfeedback.models.Project
import io.openfeedback.models.VoteStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

internal class OpenFeedbackDaoImpl(
    private val projectId: String,
    private val firestore: FirebaseFirestore
) : OpenFeedbackDao {
    override fun getProject(): Flow<Project> =
        firestore.collection("projects")
            .document(projectId)
            .snapshots
            .map { it.data() }

    override fun getUserVotes(userId: String, sessionId: String): Flow<List<String>> =
        firestore.collection("projects/$projectId/userVotes")
            .where("userId", equalTo = userId)
            .snapshots
            .map { query ->
                query.documents
                    .filter { doc ->
                        doc.get<String>("status") == VoteStatus.Active.value && doc.get<String>("talkId") == sessionId
                    }
                    .map { it.get("voteItemId") }
            }

    override suspend fun getTotalVotes(
        sessionId: String, optimisticVotes: OptimisticVotes
    ): Flow<Map<String, Long>> {
        return channelFlow {
            launch {
                firestore.collection("projects/$projectId/sessionVotes")
                    .document(sessionId)
                    .snapshots
                    .collect { documentSnapshot ->
                        val totalVotes = documentSnapshot.data(strategy = SessionVoteDeserializer())
                        optimisticVotes.lastValue = totalVotes
                        send(totalVotes)
                    }
            }
            optimisticVotes.channel.asFlow()
                .collect { send(it) }
        }
    }

    override suspend fun createVote(
        userId: String, talkId: String, voteItemId: String, status: VoteStatus
    ) {
        val ref = firestore.collection("projects")
            .document(projectId)
            .collection("userVotes")
            .document
        val vote = mapOf(
            "id" to ref.id,
            "projectId" to projectId,
            "status" to status.value,
            "talkId" to talkId,
            "userId" to userId,
            "voteItemId" to voteItemId,
            "createdAt" to FieldValue.serverTimestamp,
            "updatedAt" to FieldValue.serverTimestamp
        )
        ref.set(vote, merge = true)
    }

    override suspend fun updateVote(documentId: String, status: VoteStatus) {
        firestore.collection("projects/$projectId/userVotes")
            .document(documentId).update(
                mapOf(
                    "updatedAt" to Clock.System.now(),
                    "status" to status.value
                )
            )
    }

    override suspend fun documentIdOfVote(
        userId: String, talkId: String, voteItemId: String
    ): String? {
        val collectionReference = firestore.collection("projects/$projectId/userVotes")
        val querySnapshot = collectionReference
            .where("userId", equalTo = userId)
            .where("talkId", equalTo = talkId)
            .where("voteItemId", equalTo = voteItemId)
            .get()

        if (querySnapshot.documents.size != 1) {
            // TODO print log here
            println("Too many votes registered for $userId")
        }
        if (querySnapshot.documents.isEmpty()) {
            return null
        } else {
            return querySnapshot.documents[0].id
        }
    }
}
