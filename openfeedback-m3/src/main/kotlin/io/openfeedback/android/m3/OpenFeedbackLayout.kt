package io.openfeedback.android.m3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.openfeedback.android.viewmodels.models.UIComment
import io.openfeedback.android.viewmodels.models.UIDot
import io.openfeedback.android.viewmodels.models.UISessionFeedback
import io.openfeedback.android.viewmodels.models.UIVoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFeedback(
    config: OpenFeedbackFirebaseConfig,
    projectId: String,
    sessionId: String,
    modifier: Modifier = Modifier,
    columnCount: Int = 2,
    loading: @Composable () -> Unit = { Loading(modifier = modifier) }
) {
    val systemConfig = LocalConfiguration.current
    val viewModel: OpenFeedbackViewModel = viewModel(
        factory = OpenFeedbackViewModel.Factory.create(
            firebaseApp = config.firebaseApp,
            projectId = projectId,
            sessionId = sessionId,
            locale = systemConfig.locale
        )
    )
    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
        is OpenFeedbackUiState.Loading -> loading()
        is OpenFeedbackUiState.Success -> {
            val session = (uiState.value as OpenFeedbackUiState.Success).session
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
                        value = session.commentValue,
                        onValueChange = viewModel::valueChangedComment,
                        onSubmit = viewModel::submitComment,
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
            voteItems = sessionFeedback.voteItem,
            columnCount = columnCount,
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            content = content
        )
        if (sessionFeedback.commentVoteItemId != null) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun OpenFeedbackLayoutPreview() {
    MaterialTheme {
        OpenFeedbackLayout(
            sessionFeedback = UISessionFeedback(
                commentValue = "",
                commentVoteItemId = "",
                comments = listOf(
                    UIComment(
                        id = "",
                        voteItemId = "",
                        message = "Nice comment",
                        createdAt = "08 August 2023",
                        upVotes = 8,
                        dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                        votedByUser = true
                    ),
                    UIComment(
                        id = "",
                        voteItemId = "",
                        message = "Another one",
                        createdAt = "08 August 2023",
                        upVotes = 0,
                        dots = listOf(UIDot(x = .5f, y = .5f, color = "FF00CC")),
                        votedByUser = true
                    )
                ),
                voteItem = listOf(
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            commentInput = {
                CommentInput(value = "", onValueChange = {}, onSubmit = {})
            },
            comment = { Comment(comment = it, onClick = {}) }
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
