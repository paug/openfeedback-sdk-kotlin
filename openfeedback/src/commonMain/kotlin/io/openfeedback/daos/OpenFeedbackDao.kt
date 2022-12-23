package io.openfeedback.daos

import io.openfeedback.OptimisticVotes
import io.openfeedback.models.Project
import io.openfeedback.models.VoteStatus
import kotlinx.coroutines.flow.Flow

interface OpenFeedbackDao {
    fun getProject(): Flow<Project>
    fun getUserVotes(userId: String, sessionId: String): Flow<List<String>>
    fun getTotalVotes(sessionId: String, optimisticVotes: OptimisticVotes): Flow<Map<String, Long>>
    fun createVote(userId: String, talkId: String, voteItemId: String, status: VoteStatus)
    fun updateVote(documentId: String, status: VoteStatus)
    suspend fun documentIdOfVote(userId: String, talkId: String, voteItemId: String): String?
}

expect fun createDao(projectId: String, app: FirebaseApp): OpenFeedbackDao
