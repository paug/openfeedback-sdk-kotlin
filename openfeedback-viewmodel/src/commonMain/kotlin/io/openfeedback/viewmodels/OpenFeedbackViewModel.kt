package io.openfeedback.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.vanniktech.locale.Locale
import dev.gitlive.firebase.FirebaseApp
import io.openfeedback.OpenFeedbackRepository
import io.openfeedback.model.SessionData
import io.openfeedback.ui.models.UIComment
import io.openfeedback.ui.models.UISessionFeedback
import io.openfeedback.ui.models.UIVoteItem
import io.openfeedback.viewmodels.extensions.mapWithPreviousValue
import io.openfeedback.viewmodels.mappers.toUISessionFeedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

sealed class OpenFeedbackUiState {
    data object Loading : OpenFeedbackUiState()
    class Success(val session: UISessionFeedback) : OpenFeedbackUiState()
}

class OpenFeedbackViewModel private constructor(
    firebaseApp: FirebaseApp,
    projectId: String,
    sessionId: String,
    locale: Locale
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
                            locale = locale,
                            oldVoteItems = null,
                            oldComments = null
                        )
                    } else {
                        cur.toUISessionFeedback(
                            locale = locale,
                            oldVoteItems = prev.voteItems,
                            oldComments = prev.comments
                        )
                    }
                }
                .collect {
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
