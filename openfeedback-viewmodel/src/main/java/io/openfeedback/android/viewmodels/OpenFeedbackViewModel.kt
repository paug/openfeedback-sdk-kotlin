package io.openfeedback.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.FirebaseApp
import io.openfeedback.android.OpenFeedbackRepository
import io.openfeedback.android.caches.OptimisticVoteCaching
import io.openfeedback.android.model.VoteStatus
import io.openfeedback.android.sources.OpenFeedbackAuth
import io.openfeedback.android.sources.OpenFeedbackFirestore
import io.openfeedback.android.viewmodels.mappers.convertToUiSessionFeedback
import io.openfeedback.android.viewmodels.models.UIComment
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UISessionFeedbackWithColors
import io.openfeedback.android.viewmodels.models.UIVoteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale

sealed class OpenFeedbackUiState {
    data object Loading : OpenFeedbackUiState()
    class Success(val session: UISessionFeedback) : OpenFeedbackUiState()
}

class OpenFeedbackViewModel(
    private val firebaseApp: FirebaseApp,
    private val projectId: String,
    private val sessionId: String,
    private val locale: Locale
) : ViewModel() {
    private val repository = OpenFeedbackRepository(
        auth = OpenFeedbackAuth.Factory.create(firebaseApp),
        firestore = OpenFeedbackFirestore.Factory.create(firebaseApp),
        optimisticVoteCaching = OptimisticVoteCaching()
    )

    private val _uiState = MutableStateFlow<OpenFeedbackUiState>(OpenFeedbackUiState.Loading)
    val uiState: StateFlow<OpenFeedbackUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                flow = repository.project(projectId),
                flow2 = repository.userVotes(projectId, sessionId),
                flow3 = repository.totalVotes(projectId, sessionId),
                transform = { project, votes, totals ->
                    UISessionFeedbackWithColors(
                        convertToUiSessionFeedback(project, votes, totals, locale),
                        project.chipColors
                    )
                }
            ).collect {
                val oldSession =
                    if (uiState.value is OpenFeedbackUiState.Success) (uiState.value as OpenFeedbackUiState.Success).session
                    else null
                _uiState.value = OpenFeedbackUiState.Success(
                    it.convertToUiSessionFeedback(oldSession)
                )
            }
        }
    }

    fun valueChangedComment(value: String) {
        if (_uiState.value !is OpenFeedbackUiState.Success) return
        val session = (_uiState.value as OpenFeedbackUiState.Success).session
        _uiState.value = OpenFeedbackUiState.Success(session.copy(commentValue = value))
    }

    fun submitComment() = viewModelScope.launch {
        if (_uiState.value !is OpenFeedbackUiState.Success) return@launch
        val session = (_uiState.value as OpenFeedbackUiState.Success).session
        if (session.commentVoteItemId == null) return@launch
        repository.newComment(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = session.commentVoteItemId,
            status = VoteStatus.Active,
            text = session.commentValue
        )
    }

    fun vote(voteItem: UIVoteItem) = viewModelScope.launch {
        repository.setVote(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = voteItem.id,
            status = if (!voteItem.votedByUser) VoteStatus.Active else VoteStatus.Deleted
        )
    }

    fun upVote(comment: UIComment) = viewModelScope.launch {
        repository.upVote(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = comment.voteItemId,
            voteId = comment.id,
            status = if (!comment.votedByUser) VoteStatus.Active else VoteStatus.Deleted
        )
    }

    object Factory {
        fun create(
            firebaseApp: FirebaseApp,
            projectId: String,
            sessionId: String,
            locale: Locale
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = OpenFeedbackViewModel(
                firebaseApp = firebaseApp,
                projectId = projectId,
                sessionId = sessionId,
                locale = locale
            ) as T
        }
    }
}
