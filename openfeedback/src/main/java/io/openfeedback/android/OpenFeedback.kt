package io.openfeedback.android

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.openfeedback.android.model.Project
import io.openfeedback.android.model.VoteStatus
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
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

    class OptimisticVotes(
            var lastValue: Map<String, Long>?,
            val channel: BroadcastChannel<Map<String, Long>>
    )

    /**
     * TODO: check if this leaks
     */
    val optimisticVotes = mutableMapOf<String, OptimisticVotes>()

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

    suspend fun getProject(): Flow<Project?> = flow {
        firestore.collection("projects")
                .document(openFeedbackProjectId)
                .toFlow()
                .collect { documentSnapshot ->
                    emit(documentSnapshot.toObject(Project::class.java))
                }
    }

    fun getUserVotes(sessionId: String) = flow {
        val user = getFirebaseUser()
        if (user != null) {
            firestore.collection("projects/$openFeedbackProjectId/userVotes")
                    .whereEqualTo("userId", user.uid)
                    .toFlow()
                    .collect { querySnapshot ->
                        val votes = querySnapshot.filter {
                            it.data.get("status") == VoteStatus.Active.value
                                    && it.data.get("talkId") == sessionId
                        }.map {
                            it.data.get("voteItemId") as String
                        }
                        emit(votes)
                    }
        }
    }

    fun getTotalVotes(sessionId: String): Flow<Map<String, Long>> {
        val optimisticVotes = optimisticVotes.getOrPut(sessionId) {
            OptimisticVotes(null, BroadcastChannel(Channel.CONFLATED))
        }

        val channel = Channel<Map<String, Long>>(Channel.CONFLATED)
        val registration = firestore.collection("projects/$openFeedbackProjectId/sessionVotes")
                .document(sessionId)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    val totalVotes = documentSnapshot!!.data as? Map<String, Long>
                            ?: emptyMap() // If there's no vote yet, default to an empty map

                    optimisticVotes.lastValue = totalVotes
                    //val source = if (documentSnapshot.metadata.isFromCache) "cache" else "netwo"
                    //Log.e("TotalVotes", "Firebase vote ($source): ${totalVotes.prettyString()}")
                    channel.offer(totalVotes)
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

    suspend fun setVote(talkId: String, voteItemId: String, status: VoteStatus) = withFirebaseUser { firebaseUser ->
        val collectionReference = firestore.collection("projects/$openFeedbackProjectId/userVotes")

        val optimisticVotes = optimisticVotes.getOrPut(talkId) {
            OptimisticVotes(null, BroadcastChannel(Channel.CONFLATED))
        }

        val lastValue = optimisticVotes.lastValue
        if (lastValue != null){

            optimisticVotes.lastValue = lastValue.toMutableMap().apply {
                var count = lastValue.getOrDefault(voteItemId, 0L)
                count += if (status == VoteStatus.Deleted) -1 else 1
                if (count < 0) {
                    count = 0L
                }
                put(voteItemId, count)
            }

            //Log.e("TotalVotes", "Optimistic vote: ${optimisticVotes.lastValue?.prettyString()}")

            optimisticVotes.channel.offer(optimisticVotes.lastValue!!)
        }

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

fun Map<*, *>.prettyString() = entries.map { "${it.key}: ${it.value}" }.joinToString(separator = "\n", prefix = "\n")


