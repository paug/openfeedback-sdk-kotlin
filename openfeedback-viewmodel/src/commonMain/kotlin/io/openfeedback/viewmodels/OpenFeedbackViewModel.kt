package io.openfeedback.viewmodels

import com.vanniktech.locale.Locale
import dev.gitlive.firebase.FirebaseApp
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.openfeedback.model.Comment
import io.openfeedback.model.CommentsMap
import io.openfeedback.model.Project
import io.openfeedback.model.SessionThing
import io.openfeedback.model.UserVote
import io.openfeedback.model.VoteItemCount
import io.openfeedback.model.VoteStatus
import io.openfeedback.sources.OpenFeedbackAuth
import io.openfeedback.sources.OpenFeedbackFirestore
import io.openfeedback.viewmodels.models.UIComment
import io.openfeedback.viewmodels.models.UIDot
import io.openfeedback.viewmodels.models.UISessionFeedback
import io.openfeedback.viewmodels.models.UIVoteItem
import io.openfeedback.viewmodels.models.commentVoteItemId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.random.Random

sealed class OpenFeedbackUiState {
    data object Loading : OpenFeedbackUiState()
    class Success(val session: UISessionFeedback) : OpenFeedbackUiState()
}

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

internal data class SessionData(
    val project: Project,
    val userId: String,
    val votedItemIds: Set<String>,
    val votedCommentIds: Set<String>,
    /**
     * The aggregate counter for voteItems.
     * The counter for comments is in [comments]
     *
     * key is a voteItemId
     */
    val voteItemAggregates: Map<String, Long>,
    val comments: List<Comment>,
)

/**
 * Turns the openfeedback model into something that is a bit more palatable
 */
private fun sessionData(
    userId: String,
    project: Project,
    userVotes: List<UserVote>,
    sessionThings: Map<String, SessionThing>,
): SessionData {
    val votedItemIds = userVotes.mapNotNull {
        if (it.text != null) {
            // This is a comment
            return@mapNotNull null
        }
        if (it.voteItemId == project.commentVoteItemId()) {
            // In theory one cannot vote on the "text" vote item but 🤷
            return@mapNotNull null
        }
        it.voteItemId
    }.toSet()
    val votedCommentIds = userVotes.mapNotNull {
        if (it.text != null) {
            // This is a comment
            return@mapNotNull null
        }
        /**
         * Do we need to check voteItemId?
         */
//        if (it.voteItemId != project.commentVoteItemId()) {
//            return@mapNotNull null
//        }
        // If it.id is not null, it's the upvote for a comment
        it.id
    }.toSet()

    val voteItemAggregates = project.voteItems.mapNotNull { voteItem ->
        if (voteItem.type == "text") {
            return@mapNotNull null
        }

        val existing = sessionThings.get(voteItem.id)
        if (existing != null && existing is VoteItemCount) {
            /**
             * Be robust to negative votes
             */
            val minValue = if (votedCommentIds.contains(voteItem.id)) {
                1L
            } else {
                0L
            }
            voteItem.id to existing.count.coerceAtLeast(minValue)
        } else {
            /**
             * No document yet, return 0
             */
            voteItem.id to 0L
        }
    }.toMap()

    val commentsMaps = sessionThings.values.filterIsInstance<CommentsMap>()
    if (commentsMaps.size > 1) {
        val keys = sessionThings.filter { it.value is CommentsMap }.keys
        println("Several comment maps for voteItemIds = '$keys'.")
    }
    val commentMap = (commentsMaps.firstOrNull() ?: CommentsMap(emptyMap())).coerceAggregations(votedCommentIds)

    val comments = commentMap.all.values.sortedByDescending { it.updatedAt }
    return SessionData(
        project = project,
        userId = userId,
        votedItemIds = votedItemIds,
        votedCommentIds = votedCommentIds,
        voteItemAggregates = voteItemAggregates,
        comments = comments
    )
}

private fun CommentsMap.coerceAggregations(votedCommentIds: Set<String>): CommentsMap {
    return CommentsMap(all.mapValues {
        val minValue = if (votedCommentIds.contains(it.key)) {
            1L
        } else {
            0L
        }
        it.value.copy(plus = it.value.plus.coerceAtLeast(minValue))
    })
}
private fun SessionData.voteItem(voteItemId: String, voted: Boolean): SessionData {
    val newVotedItemsIds = if (voted) {
        votedItemIds + voteItemId
    } else {
        votedItemIds - voteItemId
    }

    val newAggregates = voteItemAggregates.mapValues {
        if (it.key == voteItemId) {
            if (voted) {
                it.value + 1
            } else {
                it.value - 1
            }
        } else {
            it.value
        }
    }
    return copy(
        votedItemIds = newVotedItemsIds,
        voteItemAggregates = newAggregates
    )
}

private fun SessionData.voteComment(commentId: String, voted: Boolean): SessionData {
    val newVotedCommentIds = if (voted) {
        votedCommentIds + commentId
    } else {
        votedCommentIds - commentId
    }

    val newComments = comments.map {
        if (it.id == commentId) {
            if (voted) {
                it.copy(plus = it.plus + 1)
            } else {
                it.copy(plus = it.plus - 1)
            }
        } else {
            it
        }
    }
    return copy(
        votedCommentIds = newVotedCommentIds,
        comments = newComments
    )
}
private fun SessionData.commitComment(text: String): SessionData {
    var found = false
    var newComments = comments.map {
        if (it.userId == userId) {
            found = true
            it.copy(updatedAt = Clock.System.now(), text = text)
        } else {
            it
        }
    }
    if (!found) {
        newComments = newComments + Comment(
            id = "placeholderId",
            userId = userId,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            text = text,
            plus = 0
        )
    }
    return copy(
        comments = newComments.sortedByDescending { it.updatedAt }
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class OpenFeedbackViewModel(
    firebaseApp: FirebaseApp,
    private val projectId: String,
    private val sessionId: String,
    private val locale: Locale
) : ViewModel() {
    private val auth = OpenFeedbackAuth(firebaseApp)
    private val firestore = OpenFeedbackFirestore.create(firebaseApp)
    private var commentVoteItemId: String? = null

    private val voteEvents = MutableSharedFlow<Event>()

    private val _uiState = MutableStateFlow<OpenFeedbackUiState>(OpenFeedbackUiState.Loading)
    val uiState: StateFlow<OpenFeedbackUiState> = _uiState

    init {
        /**
         * Warning: This screen is not 100% reactive because there are 2 sources of truth for votes:
         * - userVotes are written by the app
         * - sessionVotes is written by the backend which computes the aggregates
         *
         * We used to be reactive but this creates a blinking effect because there's a long delay until the cloud
         * function updates sessionVotes.
         *
         * Instead, just retrieve the data from the network once and use local votes.
         * There is no feedback if a given vote fails.
         *
         * See also https://stackoverflow.com/questions/58840642/set-update-collection-or-document-but-only-locally
         */
        viewModelScope.launch {
            combine(
                firestore.project(projectId),
                firestore.userVotes(
                    projectId = projectId,
                    userId = auth.userId(),
                    sessionId = sessionId,
                ),
                firestore.sessionThings(projectId = projectId, sessionId = sessionId),
            ) { project, userVotesResult, sessionThingsResult ->
                sessionData(
                    auth.userId(),
                    project,
                    userVotesResult.data,
                    sessionThingsResult.data,
                )
            }.filterNotNull()
                /**
                 * Take only the first (maybe cached) item. Meaning we might be a bit stale sometimes but this prevents
                 * the network result to kick in with completely different results after the fact, which can be surprising
                 */
                .filterFirst()
                .flatMapLatest { sessionData ->
                    /**
                     * Remember the commentVoteItemId
                     */
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
                }.mapWithPreviousValue<SessionData, UISessionFeedback> { prev, cur ->
                    if (prev == null) {
                        cur.toUISessionFeedback(
                            locale,
                            null,
                            null
                        )
                    } else {
                        cur.toUISessionFeedback(
                            locale,
                            prev.voteItems,
                            prev.comments
                        )
                    }
                }.collect {
                    _uiState.value = OpenFeedbackUiState.Success(it)
                }
        }
    }

    fun submitComment(text: String) = viewModelScope.launch {
        if (commentVoteItemId == null) {
            println("No commentVoteItemId")
            return@launch
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

    fun vote(voteItem: UIVoteItem) = viewModelScope.launch {
        voteEvents.emit(
            VoteItemEvent(
                voteItemId = voteItem.id,
                votedByUser = !voteItem.votedByUser
            )
        )
        firestore.setVote(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = voteItem.id,
            status = if (!voteItem.votedByUser) VoteStatus.Active else VoteStatus.Deleted,
            userId = auth.userId()
        )
    }

    fun upVote(comment: UIComment) = viewModelScope.launch {
        voteEvents.emit(
            VoteCommentEvent(
                commentId = comment.id,
                !comment.votedByUser
            )
        )
        if (commentVoteItemId == null) {
            println("No commentVoteItemId yet")
        }
        firestore.upVote(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = commentVoteItemId ?: "oopsie",
            voteId = comment.id,
            status = if (!comment.votedByUser) VoteStatus.Active else VoteStatus.Deleted,
            userId = auth.userId()
        )
    }
}

/**
 * Allows access to the previous emitted value
 * We use that to have stable dots coordinates
 */
private fun <T, R : Any> Flow<T>.mapWithPreviousValue(block: (previous: R?, current: T) -> R): Flow<R> {
    var prev: R? = null
    return flow {
        this@mapWithPreviousValue.collect {
            block(prev, it).also {
                emit(it)
                prev = it
            }
        }
    }
}

/**
 * A variation of take(1) that does not cancel the flow so that the query continues running and
 * network results get written
 */
private fun <T> Flow<T>.filterFirst(): Flow<T> {
    var first = true

    return mapNotNull {
        if (first) {
            first = false
            it
        } else {
            null
        }
    }
}

private fun SessionData.toUISessionFeedback(
    locale: Locale,
    oldVoteItems: List<UIVoteItem>?,
    oldComments: List<UIComment>?,
): UISessionFeedback {
    val sessionData = this
    val votedItemIds = sessionData.votedItemIds
    return UISessionFeedback(
        voteItems = sessionData.project.voteItems
            .filter { it.type == "boolean" }
            .map { voteItem ->
                val oldVoteItem = oldVoteItems?.firstOrNull { it.id == voteItem.id }
                val count = sessionData.voteItemAggregates[voteItem.id]?.toInt() ?: 0
                val oldDots = oldVoteItem?.dots.orEmpty()
                val diff = count - oldDots.size
                val dots = if (diff > 0) {
                    oldDots + newDots(diff, sessionData.project.chipColors)
                } else {
                    oldDots.dropLast(diff.absoluteValue)
                }
                UIVoteItem(
                    id = voteItem.id,
                    text = voteItem.localizedName(locale.language.code),
                    dots = dots,
                    votedByUser = votedItemIds.contains(voteItem.id)
                )
            },
        comments = sessionData.comments.map { commentItem ->
            val localDateTime =
                commentItem.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            val oldComment = oldComments?.firstOrNull { it.id == commentItem.id }
            val oldDots = oldComment?.dots.orEmpty()
            val diff = commentItem.plus.toInt() - oldDots.size
            val dots = if (diff > 0) {
                oldDots + newDots(diff, sessionData.project.chipColors)
            } else {
                oldDots.dropLast(diff.absoluteValue)
            }
            UIComment(
                id = commentItem.id,
                message = commentItem.text,
                createdAt = localDateTime.format(dateFormat),
                upVotes = commentItem.plus.toInt(),
                dots = dots,
                votedByUser = sessionData.votedCommentIds.contains(commentItem.id),
                fromUser = commentItem.userId == sessionData.userId
            )
        },
        colors = sessionData.project.chipColors
    )
}

internal fun newDots(count: Int, possibleColors: List<String>): List<UIDot> = 0.until(count).map {
    UIDot(
        Random.nextFloat(),
        Random.nextFloat().coerceIn(0.1f, 0.9f),
        possibleColors[Random.nextInt().absoluteValue % possibleColors.size]
    )
}

private val dateFormat = LocalDateTime.Format {
    dayOfMonth()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    chars(", ")
    hour()
    char(':')
    minute()
}
