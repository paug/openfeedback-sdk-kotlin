package io.openfeedback.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.openfeedback.viewmodels.models.UIComment
import io.openfeedback.viewmodels.models.UISessionFeedback
import io.openfeedback.viewmodels.models.UIVoteItem
import io.openfeedback.viewmodels.toFirebaseApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFeedback(
    config: OpenFeedbackFirebaseConfig,
    projectId: String,
    sessionId: String,
    modifier: Modifier = Modifier,
    columnCount: Int = 2,
    locale: Locale = Locale.from(Locales.currentLocaleString()),
    loading: @Composable () -> Unit = { Loading(modifier = modifier) }
) {
    val viewModel: OpenFeedbackViewModel = getViewModel(
        key = sessionId,
        factory = viewModelFactory {
            OpenFeedbackViewModel(
                firebaseApp = config.toFirebaseApp(),
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
            var text by remember { mutableStateOf("") }

            OpenFeedbackLayout(
                sessionFeedback = session,
                modifier = modifier,
                columnCount = columnCount,
                comment = {
                    Comment(
                        comment = it,
                        onClick = viewModel::upVote
                    )
                },
                commentInput = {
                    CommentInput(
                        value = text,
                        onValueChange = { text = it },
                        onSubmit = { viewModel.submitComment(text) },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                content = {
                    VoteCard(
                        voteModel = it,
                        onClick = viewModel::vote,
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
    comment: @Composable ColumnScope.(UIComment) -> Unit,
    commentInput: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.(UIVoteItem) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        VoteItems(
            voteItems = sessionFeedback.voteItems,
            columnCount = columnCount,
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            content = content
        )
        if (sessionFeedback.comments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            CommentItems(
                comments = sessionFeedback.comments,
                verticalArrangement = verticalArrangement,
                commentInput = commentInput,
                comment = comment
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PoweredBy()
        }
    }
}
