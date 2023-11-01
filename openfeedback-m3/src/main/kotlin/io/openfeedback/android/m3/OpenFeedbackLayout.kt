package io.openfeedback.android.m3

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.openfeedback.android.viewmodels.OpenFeedbackFirebaseConfig
import io.openfeedback.android.viewmodels.OpenFeedbackUiState
import io.openfeedback.android.viewmodels.OpenFeedbackViewModel
import io.openfeedback.android.viewmodels.models.UIDot
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UIVoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFeedback(
    config: OpenFeedbackFirebaseConfig,
    projectId: String,
    sessionId: String,
    language: String,
    modifier: Modifier = Modifier,
    loading: @Composable () -> Unit = { Loading(modifier = modifier) }
) {
    val configuration = LocalConfiguration.current
    val viewModel: OpenFeedbackViewModel = viewModel(
        factory = OpenFeedbackViewModel.Factory.create(
            firebaseApp = config.firebaseApp,
            projectId = projectId,
            sessionId = sessionId,
            language = language
        )
    )
    val columnCount = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
        is OpenFeedbackUiState.Loading -> loading()
        is OpenFeedbackUiState.Success -> {
            val session = (uiState.value as OpenFeedbackUiState.Success).session
            OpenFeedbackLayout(
                sessionFeedback = session,
                modifier = modifier,
                columnCount = columnCount,
                content = {
                    VoteCard(
                        voteModel = it,
                        onClick = { voteItem -> viewModel.vote(voteItem = voteItem) },
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFeedbackLayout(
    sessionFeedback: UISessionFeedback,
    modifier: Modifier = Modifier,
    columnCount: Int = 2,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    content: @Composable ColumnScope.(UIVoteItem) -> Unit
) {
    Column(modifier = modifier) {
        VoteItems(
            voteItems = sessionFeedback.voteItem,
            columnCount = columnCount,
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            content = content
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun OpenFeedbackLayoutPreview() {
    MaterialTheme {
        OpenFeedbackLayout(
            sessionFeedback = UISessionFeedback(
                comments = emptyList(),
                listOf(
                    UIVoteItem(
                        id = "",
                        text = "Fun",
                        dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                        votedByUser = true
                    ),
                    UIVoteItem(
                        id = "",
                        text = "Fun",
                        dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                        votedByUser = true
                    )
                )
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VoteCard(
                voteModel = it,
                onClick = {},
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        }
    }
}
