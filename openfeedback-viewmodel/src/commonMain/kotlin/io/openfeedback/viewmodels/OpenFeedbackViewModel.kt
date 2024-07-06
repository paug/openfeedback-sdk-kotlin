package io.openfeedback.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.vanniktech.locale.Locale
import dev.gitlive.firebase.FirebaseApp
import io.openfeedback.OpenFeedbackRepository
import io.openfeedback.model.SessionData
import io.openfeedback.viewmodels.models.UIComment
import io.openfeedback.viewmodels.models.UIDot
import io.openfeedback.viewmodels.models.UISessionFeedback
import io.openfeedback.viewmodels.models.UIVoteItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.reflect.KClass

sealed class OpenFeedbackUiState {
    data object Loading : OpenFeedbackUiState()
    class Success(val session: UISessionFeedback) : OpenFeedbackUiState()
}

class OpenFeedbackViewModel private constructor(
    firebaseApp: FirebaseApp,
    projectId: String,
    sessionId: String,
    private val locale: Locale
) : ViewModel() {
    private val repository = OpenFeedbackRepository(firebaseApp, projectId, sessionId)

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
            repository
                .fetchSessionData()
                .mapWithPreviousValue<SessionData, UISessionFeedback> { prev, cur ->
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
        repository.submitComment(text)
    }

    fun vote(voteItem: UIVoteItem) = viewModelScope.launch {
        repository.vote(voteItem.id, voteItem.votedByUser)
    }

    fun upVote(comment: UIComment) = viewModelScope.launch {
        repository.upVote(comment.id, comment.votedByUser)
    }

    companion object {
        fun provideFactory(
            firebaseApp: FirebaseApp,
            projectId: String,
            sessionId: String,
            locale: Locale
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                OpenFeedbackViewModel(firebaseApp, projectId, sessionId, locale) as T
        }
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
