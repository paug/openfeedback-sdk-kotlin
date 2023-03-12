package io.openfeedback.daos

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.firestore.firestore
import io.openfeedback.OptimisticVotes
import io.openfeedback.models.Project
import io.openfeedback.models.VoteStatus
import kotlinx.coroutines.flow.Flow

interface OpenFeedbackDao {
    fun getProject(): Flow<Project>
    fun getUserVotes(userId: String, sessionId: String): Flow<List<String>>
    suspend fun getTotalVotes(sessionId: String, optimisticVotes: OptimisticVotes): Flow<Map<String, Long>>
    suspend fun createVote(userId: String, talkId: String, voteItemId: String, status: VoteStatus)
    suspend fun updateVote(documentId: String, status: VoteStatus)
    suspend fun documentIdOfVote(userId: String, talkId: String, voteItemId: String): String?

    object Factory {
        fun createDao(projectId: String, app: FirebaseApp): OpenFeedbackDao {
            val firestore = Firebase.firestore(app)
            firestore.setSettings(persistenceEnabled = true)
            return OpenFeedbackDaoImpl(projectId, firestore)
        }
    }
}
