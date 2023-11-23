package io.openfeedback

import io.openfeedback.caches.OptimisticVoteCaching
import io.openfeedback.model.Project
import io.openfeedback.model.SessionVotes
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteStatus
import io.openfeedback.sources.OpenFeedbackAuth
import io.openfeedback.sources.OpenFeedbackFirestore
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
    fun userVotes(projectId: String, sessionId: String): Flow<List<UserVote>> =
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

    suspend fun newComment(projectId: String, talkId: String, voteItemId: String, status: VoteStatus, text: String) {
        auth.withFirebaseUser {
            firestore.newComment(projectId, it.uid, talkId, voteItemId, status, text)
        }
    }

    suspend fun setVote(projectId: String, talkId: String, voteItemId: String, status: VoteStatus) {
        auth.withFirebaseUser {
            optimisticVoteCaching.updateVotes(voteItemId, status)
            firestore.setVote(projectId, it.uid, talkId, voteItemId, status)
        }
    }

    suspend fun upVote(
        projectId: String,
        talkId: String,
        voteItemId: String,
        voteId: String,
        status: VoteStatus
    ) {
        auth.withFirebaseUser {
            optimisticVoteCaching.updateCommentVote(voteId, status)
            firestore.upVote(projectId, it.uid, talkId, voteItemId, voteId, status)
        }
    }
}
