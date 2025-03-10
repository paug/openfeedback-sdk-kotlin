// See https://github.com/GitLiveApp/firebase-kotlin-sdk/issues/710
@file:Suppress(
    "CANNOT_OVERRIDE_INVISIBLE_MEMBER",
    "INVISIBLE_MEMBER",
    "INVISIBLE_REFERENCE",
)
package io.openfeedback.sources

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import io.openfeedback.mappers.timestampToInstant
import io.openfeedback.model.Comment
import io.openfeedback.model.CommentsMap
import io.openfeedback.model.Project
import io.openfeedback.model.SessionThing
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteItemCount
import io.openfeedback.model.VoteStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

internal class UserVotesResult(val data: List<UserVote>, val isFromCache: Boolean)
internal class SessionThingsResult(val data: Map<String, SessionThing>, val isFromCache: Boolean)

@Suppress("UNCHECKED_CAST")
internal class OpenFeedbackFirestore(private val firestore: FirebaseFirestore) {
    fun project(projectId: String): Flow<Project> =
        firestore.collection("projects")
            .document(projectId)
            .snapshots
            .map { querySnapshot -> querySnapshot.data<Project>() }

    fun userVotes(projectId: String, userId: String, sessionId: String): Flow<UserVotesResult> =
        firestore.collection("projects/$projectId/userVotes")
            .where { "userId" equalTo userId }
            .where { "status" equalTo VoteStatus.Active.value }
            .where { "talkId" equalTo sessionId }
            .snapshots
            .map { querySnapshot ->
                var isFromCache = true
                val userVotes = querySnapshot.documents.map {
                    if (!it.metadata.isFromCache) {
                        isFromCache = false
                    }
                    it.data<UserVote>()
                }
                UserVotesResult(
                    userVotes,
                    isFromCache
                )
            }


    private fun Map<*, *>.toComment(id: String): Comment = Comment(
        id = id,
        text = this["text"] as String,
        plus = (this["plus"] as Long).coerceAtLeast(0),
        createdAt = timestampToInstant(this["createdAt"]!!),
        updatedAt = timestampToInstant(this["updatedAt"]!!),
        userId = this["userId"] as String
    )

    /**
     * For some weird reasons, OpenFeedback can return empty map for some comments.
     * To avoid a crash when the comment is parsed in [toComment] function, we check
     * that the map is not empty.
     */
    private fun <K, V> Map<K, V>.filterMapNotEmpty(): Map<K, V> =
        filter { it.value is Map<*, *> && (it.value as Map<K, V>).isNotEmpty() }

    private fun Map<String, *>.toCommentsMap(): CommentsMap {
        val comments = this
            .filterMapNotEmpty()
            .mapValues { (it.value as Map<*, *>).toComment(it.key) }
        return CommentsMap(comments)
    }

    /**
     * Return all things related to this session, vote counts and comments
     */
    fun sessionThings(projectId: String, sessionId: String): Flow<SessionThingsResult> =
        firestore.collection("projects/$projectId/sessionVotes")
            .document(sessionId)
            .snapshots
            .mapNotNull { documentSnapshot ->
                if (documentSnapshot.exists.not()) {
                    return@mapNotNull SessionThingsResult(
                        emptyMap(),
                        documentSnapshot.metadata.isFromCache
                    )
                }
                // See https://github.com/GitLiveApp/firebase-kotlin-sdk/issues/710
                val sessionData = documentSnapshot.encodedData()
                sessionData as Map<String, Any?>
                val sessionThings = sessionData.mapValues {
                    if (it.value is Long) {
                        VoteItemCount(it.value as Long)
                    } else if (it.value is Map<*, *>) {
                        (it.value as Map<String, *>).toCommentsMap()
                    } else {
                        error("expected a long or a map of comments, got '$this'")
                    }
                }

                SessionThingsResult(sessionThings, documentSnapshot.metadata.isFromCache)
            }

    suspend fun setComment(
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
            .where { "userId" equalTo userId }
            .where { "talkId" equalTo talkId }
            .where { "voteItemId" equalTo voteItemId }
            .get()
        /**
         * XXX: There may be a race here where we create 2 documents, not really sure under which circumstances
         */
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
                    "text" to text.trim(),
                )
            )
        } else {
            querySnapshot.documents[0]
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
            .where { "userId" equalTo userId }
            .where { "talkId" equalTo talkId }
            .where { "voteItemId" equalTo voteItemId }
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
            .where { "userId" equalTo userId }
            .where { "talkId" equalTo talkId }
            .where { "voteItemId" equalTo voteItemId }
            .where { "voteId" equalTo voteId }
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
