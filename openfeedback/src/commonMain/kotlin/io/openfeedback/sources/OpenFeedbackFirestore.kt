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
import dev.gitlive.firebase.firestore.FirebaseFirestoreSettings
import dev.gitlive.firebase.firestore.LocalCacheSettings
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.firestoreSettings
import io.openfeedback.mappers.timestampToInstant
import io.openfeedback.model.Comment
import io.openfeedback.model.CommentsMap
import io.openfeedback.model.Project
import io.openfeedback.model.RawUserVote
import io.openfeedback.model.SessionThing
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteItemCount
import io.openfeedback.model.VoteStatus
import io.openfeedback.model.toUserVote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
            // Include hidden votes so the user's own comment can be displayed even if
            // it's hidden by the moderation.
            .where { "status" inArray listOf(VoteStatus.Active.value, VoteStatus.Hidden.value) }
            .where { "talkId" equalTo sessionId }
            .snapshots
            .map { querySnapshot ->
                var isFromCache = true
                val userVotes = querySnapshot.documents.map {
                    if (!it.metadata.isFromCache) {
                        isFromCache = false
                    }
                    it.data<RawUserVote>().toUserVote()
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
            .map { documentSnapshot ->
                if (documentSnapshot.exists.not()) {
                    return@map SessionThingsResult(
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

    /**
     * Create or update a text vote.
     *
     * @param projectId the project id
     * @param userId the user id
     * @param talkId the talk id
     * @param voteItemId the vote item id
     * @param text the text vote content
     * @return `true` if the comment was created / updated and the comment status is
     * [VoteStatus.Active], `false` otherwise
     */
    suspend fun setComment(
        projectId: String,
        userId: String,
        talkId: String,
        voteItemId: String,
        text: String
    ): Boolean {
        if (text.trim() == "") {
            return false
        }

        val collectionReference = firestore.collection("projects/$projectId/userVotes")
        val querySnapshot = collectionReference
            .where { "userId" equalTo userId }
            .where { "talkId" equalTo talkId }
            .where { "voteItemId" equalTo voteItemId }
            .get()

        // User votes may contain upvotes. Those upvotes are identified using the "textPlus" type.
        // The comment should be the only document whose type isn't "textPlus".
        val document = querySnapshot.documents
            .map { it.data<RawUserVote>() }
            .filterNot { it.voteType == "textPlus" }
            .firstOrNull()

        // If the comment was hidden, we shouldn't set it to Active.
        val status = when (document?.status) {
            VoteStatus.Hidden.value -> VoteStatus.Hidden
            else -> VoteStatus.Active
        }

        /**
         * XXX: There may be a race here where we create 2 documents, not really sure under which circumstances
         */
        if (document == null) {
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
            collectionReference
                .document(document.id)
                .update(
                    mapOf(
                        "updatedAt" to FieldValue.serverTimestamp,
                        "status" to status.value,
                        "text" to text.trim()
                    )
                )
        }

        return status == VoteStatus.Active
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

        private val instances = mutableMapOf<String, FirebaseFirestore>()

        fun create(app: FirebaseApp): OpenFeedbackFirestore {
            val firestore = instances.getOrPut(app.name) {
                Firebase.firestore(app).apply {
                    settings = firestoreSettings {
                        cacheSettings = LocalCacheSettings.Persistent(
                            FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED,
                        )
                    }
                }
            }

            return OpenFeedbackFirestore(firestore)
        }
    }
}
