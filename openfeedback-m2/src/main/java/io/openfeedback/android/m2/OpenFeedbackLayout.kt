package io.openfeedback.android.m2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.openfeedback.OpenFeedbackConfig
import io.openfeedback.android.viewmodels.OpenFeedbackUiState
import io.openfeedback.android.viewmodels.OpenFeedbackViewModel
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UIVoteItem

@Composable
fun OpenFeedback(
    openFeedbackState: OpenFeedbackConfig,
    sessionId: String,
    language: String,
    modifier: Modifier = Modifier,
    loading: @Composable () -> Unit = { Loading(modifier = modifier) }
) {
    val viewModel: OpenFeedbackViewModel = viewModel(
        factory = OpenFeedbackViewModel.Factory.create(openFeedbackState, sessionId, language)
    )
    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
        is OpenFeedbackUiState.Loading -> loading()
        is OpenFeedbackUiState.Success -> {
            val session = (uiState.value as OpenFeedbackUiState.Success).session
            OpenFeedbackLayout(
                sessionFeedback = session,
                modifier = modifier,
                onClick = { voteItem -> viewModel.vote(voteItem = voteItem) }
            )
        }
    }
}

@Composable
fun OpenFeedbackLayout(
    sessionFeedback: UISessionFeedback,
    modifier: Modifier = Modifier,
    onClick: (voteItem: UIVoteItem) -> Unit
) {
    Column(modifier = modifier) {
        VoteItems(voteItems = sessionFeedback.voteItem, onClick = onClick)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            PoweredBy()
        }
    }
}
