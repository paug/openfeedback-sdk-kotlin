package io.openfeedback.android.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.openfeedback.android.OpenFeedback
import io.openfeedback.android.OpenFeedbackState
import io.openfeedback.android.OpenFeedbackViewModel
import io.openfeedback.android.model.UISessionFeedback
import io.openfeedback.android.model.UIVoteItem

@Composable
fun SessionFeedbackContainer(
    openFeedbackState: OpenFeedback,
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
        is OpenFeedbackState.Loading -> loading()
        is OpenFeedbackState.Success -> {
            val session = (uiState.value as OpenFeedbackState.Success).session
            SessionFeedback(
                sessionFeedback = session,
                modifier = modifier,
                onClick = { voteItem -> viewModel.vote(voteItem = voteItem) }
            )
        }
    }
}

@Composable
fun SessionFeedback(
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

@Composable
fun rememberOpenFeedbackState(
    context: Context = LocalContext.current,
    projectId: String,
    firebaseConfig: OpenFeedback.FirebaseConfig
) = remember {
    OpenFeedback(
        context = context,
        openFeedbackProjectId = projectId,
        firebaseConfig = firebaseConfig
    )
}
