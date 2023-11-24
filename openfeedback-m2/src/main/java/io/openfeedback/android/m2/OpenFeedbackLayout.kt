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
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.openfeedback.viewmodels.OpenFeedbackFirebaseConfig
import io.openfeedback.viewmodels.OpenFeedbackUiState
import io.openfeedback.viewmodels.OpenFeedbackViewModel
import io.openfeedback.viewmodels.models.UISessionFeedback
import io.openfeedback.viewmodels.models.UIVoteItem

@Deprecated("Please use m3 artifact to display OpenFeedback")
@Composable
fun OpenFeedback(
    config: OpenFeedbackFirebaseConfig,
    projectId: String,
    sessionId: String,
    modifier: Modifier = Modifier,
    locale: Locale = Locale.from(Locales.currentLocaleString()),
    loading: @Composable () -> Unit = { Loading(modifier = modifier) }
) {
    val viewModel: OpenFeedbackViewModel = getViewModel(
        key = sessionId,
        factory = viewModelFactory {
            OpenFeedbackViewModel(
                firebaseApp = config.firebaseApp.value,
                projectId = projectId,
                sessionId = sessionId,
                locale = locale
            )
        }
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

@Deprecated("Please use m3 artifact to display OpenFeedback")
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
