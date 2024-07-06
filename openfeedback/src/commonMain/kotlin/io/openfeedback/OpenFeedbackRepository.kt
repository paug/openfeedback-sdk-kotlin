package io.openfeedback

import dev.gitlive.firebase.FirebaseApp
import io.openfeedback.extensions.commentVoteItemId
import io.openfeedback.extensions.commitComment
import io.openfeedback.extensions.filterFirst
import io.openfeedback.extensions.voteComment
import io.openfeedback.extensions.voteItem
import io.openfeedback.mappers.mapToSessionData
import io.openfeedback.model.SessionData
import io.openfeedback.model.VoteStatus
import io.openfeedback.sources.OpenFeedbackAuth
import io.openfeedback.sources.OpenFeedbackFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.scan

sealed interface Event
private class CommitComment(
    val text: String
) : Event

private class VoteItemEvent(
    val voteItemId: String,
    val votedByUser: Boolean
) : Event

private class VoteCommentEvent(
    val commentId: String,
    val votedByUser: Boolean
) : Event

class OpenFeedbackRepository(
    firebaseApp: FirebaseApp,
    val projectId: String,
    val sessionId: String,
) {
    private val auth = OpenFeedbackAuth(firebaseApp)
    private val firestore = OpenFeedbackFirestore.create(firebaseApp)
    private val voteEvents = MutableSharedFlow<Event>()
    private var commentVoteItemId: String? = null

    /**
     * Observe remote firestore database to merge the project, user votes and sessions
     * to cache vote events and vote item id of the current user.
     *
     * @return Flow for the [SessionData].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun fetchSessionData(): Flow<SessionData> = coroutineScope {
        return@coroutineScope combine(
            firestore.project(projectId),
            firestore.userVotes(
                projectId = projectId,
                userId = auth.userId(),
                sessionId = sessionId,
            ),
            firestore.sessionThings(projectId = projectId, sessionId = sessionId),
        ) { project, userVotesResult, sessionThingsResult ->
            mapToSessionData(
                auth.userId(),
                project,
                userVotesResult.data,
                sessionThingsResult.data,
            )
        }.filterNotNull()
            /*
             * Take only the first (maybe cached) item. Meaning we might be a bit stale sometimes but this prevents
             * the network result to kick in with completely different results after the fact, which can be surprising
             */
            .filterFirst()
            .flatMapLatest { sessionData ->
                // Remember the commentVoteItemId
                commentVoteItemId = sessionData.project.commentVoteItemId()

                voteEvents.scan(sessionData) { acc, value ->
                    when (value) {
                        is VoteItemEvent -> {
                            acc.voteItem(value.voteItemId, value.votedByUser)
                        }

                        is VoteCommentEvent -> {
                            acc.voteComment(value.commentId, value.votedByUser)
                        }

                        is CommitComment -> {
                            acc.commitComment(value.text)
                        }
                    }
                }
            }
    }

    /**
     * Submit a new comment for a session.
     *
     * @param text Content of the comment.
     */
    suspend fun submitComment(text: String) = coroutineScope {
        if (text == "") {
            println("Can't submit an empty comment")
            return@coroutineScope
        }
        if (commentVoteItemId == null) {
            println("No commentVoteItemId")
            return@coroutineScope
        }
        voteEvents.emit(CommitComment(text))
        firestore.setComment(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = commentVoteItemId!!,
            status = VoteStatus.Active,
            text = text,
            userId = auth.userId()
        )
    }

    /**
     * Update a vote on a vote item.
     *
     * @param voteItemId Identifier of a vote item.
     * @param votedByUser Notify if we need to active or delete the up vote.
     */
    suspend fun vote(voteItemId: String, votedByUser: Boolean) = coroutineScope {
        voteEvents.emit(VoteItemEvent(voteItemId = voteItemId, votedByUser = !votedByUser))
        firestore.setVote(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = voteItemId,
            status = if (!votedByUser) VoteStatus.Active else VoteStatus.Deleted,
            userId = auth.userId()
        )
    }

    /**
     * Up vote an existing comment.
     *
     * @param commentId Identifier of the existing comment.
     * @param votedByUser Notify if we need to active or delete the up vote.
     */
    suspend fun upVote(commentId: String, votedByUser: Boolean) = coroutineScope {
        if (commentVoteItemId == null) {
            println("No commentVoteItemId yet")
            return@coroutineScope
        }
        voteEvents.emit(VoteCommentEvent(commentId = commentId, votedByUser = !votedByUser))
        firestore.upVote(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = commentVoteItemId!!,
            voteId = commentId,
            status = if (!votedByUser) VoteStatus.Active else VoteStatus.Deleted,
            userId = auth.userId()
        )
    }
}
