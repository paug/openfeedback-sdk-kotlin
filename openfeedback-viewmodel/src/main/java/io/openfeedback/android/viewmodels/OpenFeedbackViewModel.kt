package io.openfeedback.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.openfeedback.OpenFeedbackConfig
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UIVoteItem
import io.openfeedback.models.VoteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OpenFeedbackUiState {
    object Loading : OpenFeedbackUiState()
    class Success(val session: UISessionFeedback) : OpenFeedbackUiState()
}

class OpenFeedbackViewModel(
    private val openFeedbackConfig: OpenFeedbackConfig,
    private val sessionId: String,
    private val language: String
) : ViewModel() {
    private val _uiState = MutableStateFlow<OpenFeedbackUiState>(OpenFeedbackUiState.Loading)
    val uiState: StateFlow<OpenFeedbackUiState> = _uiState

    init {
        viewModelScope.launch {
            openFeedbackConfig.getUISessionFeedback(sessionId, language).collect {
                val oldSession =
                    if (uiState.value is OpenFeedbackUiState.Success) (uiState.value as OpenFeedbackUiState.Success).session
                    else null
                _uiState.value = OpenFeedbackUiState.Success(
                    OpenFeedbackModelHelper.keepDotsPosition(
                        oldSessionFeedback = oldSession,
                        newSessionFeedback = it.session,
                        colors = it.colors
                    )
                )
            }
        }
    }

    fun vote(voteItem: UIVoteItem) = viewModelScope.launch {
        openFeedbackConfig.setVote(
            talkId = sessionId,
            voteItemId = voteItem.id,
            status = if (!voteItem.votedByUser) VoteStatus.Active else VoteStatus.Deleted
        )
    }

    object Factory {
        fun create(openFeedbackConfig: OpenFeedbackConfig, sessionId: String, language: String) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OpenFeedbackViewModel(openFeedbackConfig, sessionId, language) as T
            }
    }
}
