package io.openfeedback.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.openfeedback.android.model.UISessionFeedback
import io.openfeedback.android.model.UIVoteItem
import io.openfeedback.android.model.VoteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class OpenFeedbackState {
    object Loading : OpenFeedbackState()
    class Success(val session: UISessionFeedback) : OpenFeedbackState()
}

class OpenFeedbackViewModel(
    private val openFeedback: OpenFeedback,
    private val sessionId: String,
    private val language: String
) : ViewModel() {
    private val _uiState = MutableStateFlow<OpenFeedbackState>(OpenFeedbackState.Loading)
    val uiState: StateFlow<OpenFeedbackState> = _uiState

    init {
        viewModelScope.launch {
            openFeedback.getUISessionFeedback(sessionId, language).collect {
                val oldSession =
                    if (uiState.value is OpenFeedbackState.Success) (uiState.value as OpenFeedbackState.Success).session
                    else null
                _uiState.value = OpenFeedbackState.Success(OpenFeedbackModelHelper.keepDotsPosition(
                    oldSessionFeedback = oldSession,
                    newSessionFeedback = it.session,
                    colors = it.colors
                ))
            }
        }
    }

    fun vote(voteItem: UIVoteItem) = viewModelScope.launch {
        openFeedback.setVote(
            talkId = sessionId,
            voteItemId = voteItem.id,
            status = if (!voteItem.votedByUser) VoteStatus.Active else VoteStatus.Deleted
        )
    }

    object Factory {
        fun create(openFeedback: OpenFeedback, sessionId: String, language: String) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OpenFeedbackViewModel(openFeedback, sessionId, language) as T
            }
    }
}