package io.openfeedback.android

import io.openfeedback.android.caches.OptimisticVoteCaching
import io.openfeedback.android.model.Project
import io.openfeedback.android.model.SessionVotes
import io.openfeedback.android.model.VoteStatus
import io.openfeedback.android.sources.OpenFeedbackAuth
import io.openfeedback.android.sources.OpenFeedbackFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

class OpenFeedbackRepository(
    private val auth: OpenFeedbackAuth,
    private val firestore: OpenFeedbackFirestore,
    private val optimisticVoteCaching: OptimisticVoteCaching
) {
    fun project(projectId: String): Flow<Project> = firestore.project(projectId)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun userVotes(projectId: String, sessionId: String): Flow<List<String>> =
        flow { emit(auth.firebaseUser()) }
            .flatMapConcat {
                if (it != null) {
                    firestore.userVotes(projectId, it.uid, sessionId)
                } else {
                    emptyFlow()
                }
            }

    fun totalVotes(projectId: String, sessionId: String): Flow<SessionVotes> =
        merge(
            firestore.sessionVotes(projectId, sessionId)
                .onEach { optimisticVoteCaching.setSessionVotes(it) },
            optimisticVoteCaching.votes
        )

    suspend fun setVote(projectId: String, talkId: String, voteItemId: String, status: VoteStatus) {
        auth.withFirebaseUser {
            optimisticVoteCaching.updateVotes(voteItemId, status)
            firestore.setVote(projectId, it.uid, talkId, voteItemId, status)
        }
    }
}
