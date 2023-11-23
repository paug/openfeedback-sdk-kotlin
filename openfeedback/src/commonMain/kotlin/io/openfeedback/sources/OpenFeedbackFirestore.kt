package io.openfeedback.sources

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.SpecialValueSerializer
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import io.openfeedback.mappers.convertToModel
import io.openfeedback.model.Project
import io.openfeedback.model.SessionVotes
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OpenFeedbackFirestore(private val firestore: FirebaseFirestore) {
    fun project(projectId: String): Flow<Project> =
        firestore.collection("projects")
            .document(projectId)
            .snapshots
            .map { querySnapshot -> querySnapshot.data<Project>() }

    fun userVotes(projectId: String, userId: String, sessionId: String): Flow<List<UserVote>> =
        firestore.collection("projects/$projectId/userVotes")
            .where("userId", equalTo = userId)
            .where("status", VoteStatus.Active.value)
            .where("talkId", sessionId)
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map {
                    UserVote(
                        voteItemId = it.get<String>("voteItemId"),
                        voteId = it.get<String?>("voteId")
                    )
                }
            }

    fun sessionVotes(projectId: String, sessionId: String): Flow<SessionVotes> =
        firestore.collection("projects/$projectId/sessionVotes")
            .document(sessionId)
            .snapshots
            .map { querySnapshot ->
                querySnapshot.data(strategy = SpecialValueSerializer(
                    serialName = "SessionVotes",
                    toNativeValue = {},
                    fromNativeValue = {
                        val data = it as HashMap<String, *>
                        SessionVotes(
                            votes = data.filter { it.value is Long } as Map<String, Long>,
                            comments = data
                                .filter { it.value is HashMap<*, *> }
                                .map {
                                    val voteItemId = it.key
                                    (it.value as HashMap<*, *>).entries
                                        .filter { (it.value as Map<String, *>).isNotEmpty() }
                                        .map { entry ->
                                            entry.key as String to (entry.value as Map<String, *>)
                                                .convertToModel(
                                                    id = entry.key as String,
                                                    voteItemId = voteItemId
                                                )
                                        }
                                }
                                .flatten()
                                .associate { it.first to it.second }
                        )
                    }
                ))
            }

    suspend fun newComment(
        projectId: String,
        userId: String,
        talkId: String,
        voteItemId: String,
        status: VoteStatus,
        text: String
    ) {
        if (text.trim() == "") return
        val collectionReference = firestore.collection("projects/$projectId/userVotes")
        val querySnapshot = collectionReference
            .where("userId", equalTo = userId)
            .where("talkId", equalTo = talkId)
            .where("voteItemId", equalTo = voteItemId)
            .get()
        if (querySnapshot.documents.isEmpty()) {
            val documentReference = collectionReference.document
            documentReference.set(
                mapOf(
                    "id" to documentReference.id,
                    "createdAt" to FieldValue.serverTimestamp,
                    "projectId" to projectId,
                    "status" to status.value,
                    "talkId" to talkId,
                    "updatedAt" to FieldValue.serverTimestamp,
                    "userId" to userId,
                    "voteItemId" to voteItemId,
                    "text" to text.trim()
                )
            )
        } else {
            collectionReference
                .document(querySnapshot.documents[0].id)
                .update(
                    mapOf(
                        "updatedAt" to FieldValue.serverTimestamp,
                        "status" to status.value,
                        "text" to text.trim()
                    )
                )
        }
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
            .where("userId", equalTo = userId)
            .where("talkId", equalTo = talkId)
            .where("voteItemId", equalTo = voteItemId)
            .get()
        if (querySnapshot.documents.isEmpty()) {
            val documentReference = collectionReference.document
            documentReference.set(
                mapOf(
                    "id" to documentReference.id,
                    "createdAt" to FieldValue.serverTimestamp,
                    "projectId" to projectId,
                    "status" to status.value,
                    "talkId" to talkId,
                    "updatedAt" to FieldValue.serverTimestamp,
                    "userId" to userId,
                    "voteItemId" to voteItemId
                )
            )
        } else {
            collectionReference
                .document(querySnapshot.documents[0].id)
                .update(
                    mapOf(
                        "updatedAt" to FieldValue.serverTimestamp,
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
            .where("userId", equalTo = userId)
            .where("talkId", equalTo = talkId)
            .where("voteItemId", equalTo = voteItemId)
            .where("voteId", equalTo = voteId)
            .get()
        if (querySnapshot.documents.isEmpty()) {
            val documentReference = collectionReference.document
            documentReference.set(
                mapOf(
                    "projectId" to projectId,
                    "talkId" to talkId,
                    "voteItemId" to voteItemId,
                    "id" to documentReference.id,
                    "voteId" to voteId,
                    "createdAt" to FieldValue.serverTimestamp,
                    "updatedAt" to FieldValue.serverTimestamp,
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
                        "updatedAt" to FieldValue.serverTimestamp,
                        "status" to status.value
                    )
                )
        }
    }

    companion object Factory {
        fun create(app: FirebaseApp): OpenFeedbackFirestore {
            val firestore = Firebase.firestore(app)
            firestore.setSettings(persistenceEnabled = true)
            return OpenFeedbackFirestore(firestore)
        }
    }
}
