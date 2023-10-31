package io.openfeedback.android.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import io.openfeedback.android.FirebaseConfig
import io.openfeedback.android.OpenFeedbackRepository
import io.openfeedback.android.caches.OptimisticVoteCaching
import io.openfeedback.android.model.VoteStatus
import io.openfeedback.android.sources.FirebaseFactory
import io.openfeedback.android.sources.OpenFeedbackAuth
import io.openfeedback.android.sources.OpenFeedbackFirestore
import io.openfeedback.android.viewmodels.mappers.convertToUiSessionFeedback
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UISessionFeedbackWithColors
import io.openfeedback.android.viewmodels.models.UIVoteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class OpenFeedbackUiState {
    data object Loading : OpenFeedbackUiState()
    class Success(val session: UISessionFeedback) : OpenFeedbackUiState()
}

class OpenFeedbackViewModel(
    private val firebase: FirebaseApp,
    private val projectId: String,
    private val sessionId: String,
    private val language: String
) : ViewModel() {
    private val repository = OpenFeedbackRepository(
        auth = OpenFeedbackAuth.Factory.create(firebase),
        firestore = OpenFeedbackFirestore.Factory.create(firebase),
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
                        convertToUiSessionFeedback(project, votes, totals, language),
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

    fun vote(voteItem: UIVoteItem) = viewModelScope.launch {
        repository.setVote(
            projectId = projectId,
            talkId = sessionId,
            voteItemId = voteItem.id,
            status = if (!voteItem.votedByUser) VoteStatus.Active else VoteStatus.Deleted
        )
    }

    object Factory {
        fun create(
            context: Context,
            firebaseConfig: FirebaseConfig,
            projectId: String,
            sessionId: String,
            language: String
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = OpenFeedbackViewModel(
                firebase = FirebaseFactory.create(context, firebaseConfig),
                projectId = projectId,
                sessionId = sessionId,
                language = language
            ) as T
        }
    }
}
