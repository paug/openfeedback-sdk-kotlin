package io.openfeedback

import dev.gitlive.firebase.FirebaseApp
import io.openfeedback.daos.OpenFeedbackAuth
import io.openfeedback.daos.OpenFeedbackDao
import io.openfeedback.models.Project
import io.openfeedback.models.VoteStatus
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OpenFeedbackConfig(
    openFeedbackProjectId: String,
    firebaseApp: FirebaseApp
) {
    private val dao: OpenFeedbackDao
    private val auth: OpenFeedbackAuth
    private val optimisticVotes = mutableMapOf<String, OptimisticVotes>()

    init {
        auth = OpenFeedbackAuth.Factory.createAuth(firebaseApp)
        dao = OpenFeedbackDao.Factory.createDao(openFeedbackProjectId, firebaseApp)
    }

    private suspend fun <R> withFirebaseUser(block: suspend (String) -> R?): R? {
        return getFirebaseUser()?.let { block.invoke(it) }
    }

    private suspend fun getFirebaseUser(): String? = Mutex().withLock {
        auth.getFirebaseUser()
    }

    fun getProject(): Flow<Project> = dao.getProject()

    suspend fun getUserVotes(sessionId: String): Flow<List<String>> = withFirebaseUser {
        dao.getUserVotes(it, sessionId)
    } ?: emptyFlow()

    suspend fun getTotalVotes(sessionId: String): Flow<Map<String, Long>> {
        val optimisticVotes: OptimisticVotes = optimisticVotes.getOrPut(sessionId) {
            OptimisticVotes(null, BroadcastChannel(Channel.CONFLATED))
        }
        return dao.getTotalVotes(sessionId, optimisticVotes)
    }

    suspend fun setVote(sessionId: String, voteItemId: String, status: VoteStatus) =
        withFirebaseUser { userId ->
            val optimisticVotes = optimisticVotes.getOrPut(sessionId) {
                OptimisticVotes(null, BroadcastChannel(Channel.CONFLATED))
            }
            val lastValue = optimisticVotes.lastValue
            if (lastValue != null) {
                optimisticVotes.lastValue = lastValue.toMutableMap().apply {
                    var count = lastValue.getOrElse(voteItemId, { 0L })
                    count += if (status == VoteStatus.Deleted) -1 else 1
                    if (count < 0) {
                        count = 0L
                    }
                    put(voteItemId, count)
                }
                optimisticVotes.channel.trySend(optimisticVotes.lastValue!!)
            }
            val documentId = dao.documentIdOfVote(userId, sessionId, voteItemId)
            if (documentId == null) {
                dao.createVote(
                    userId = userId,
                    talkId = sessionId,
                    voteItemId = voteItemId,
                    status = status
                )
            } else {
                dao.updateVote(
                    documentId = documentId,
                    status = status
                )
            }
        }
}
